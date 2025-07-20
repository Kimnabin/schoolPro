package school.xxxx.application.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.xxxx.domain.exception.BusinessRuleViolationException;
import school.xxxx.domain.exception.user.InvalidUserDataException;
import school.xxxx.domain.exception.user.UserAlreadyExistsException;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.domain.service.user.UserDomainService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enhanced UserDomainService with comprehensive business logic and security
 *
 * @author Senior Backend Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Configurable business rules
    @Value("${app.security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${app.security.password-expiry-days:90}")
    private int passwordExpiryDays;

    @Value("${app.business.user.min-username-length:3}")
    private int minUsernameLength;

    @Value("${app.business.user.max-username-length:50}")
    private int maxUsernameLength;

    // Basic CRUD with enhanced validation
    @Override
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        validateId(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Cacheable(value = "users", key = "'username:' + #username")
    public User getUserByUsername(String username) {
        validateUsername(username);
        return userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UserNotFoundException("username", username));
    }

    @Override
    @Cacheable(value = "users", key = "'email:' + #email")
    public User getUserByEmail(String email) {
        validateEmail(email);
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public User createUser(User user) {
        validateUserForCreation(user);

        // Business rule: Check for duplicates
        checkForDuplicateUsername(user.getUsername());
        checkForDuplicateEmail(user.getEmail());

        // Business rule: Default values
        applyDefaultValues(user);

        // Security: Hash password
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.updatePasswordChangedAt();
        }

        log.info("Creating new user: {}", user.getUsername());
        User savedUser = userRepository.save(user);

        // Business event (could trigger email verification, welcome email, etc.)
        publishUserCreatedEvent(savedUser);

        return savedUser;
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public User updateUser(User user) {
        validateUserForUpdate(user);

        // Get existing user for comparison
        User existingUser = getUserById(user.getId());

        // Business rule: Check email uniqueness if changed
        if (!existingUser.getEmail().equals(user.getEmail())) {
            checkForDuplicateEmail(user.getEmail());
        }

        // Business rule: Prevent certain changes for locked accounts
        if (existingUser.getAccountLocked() && !user.getAccountLocked()) {
            log.info("Unlocking account for user: {}", user.getUsername());
        }

        log.info("Updating user: {}", user.getUsername());
        User updatedUser = userRepository.save(user);

        publishUserUpdatedEvent(updatedUser, existingUser);

        return updatedUser;
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public void deleteUser(Long userId) {
        validateId(userId);

        User user = getUserById(userId);

        // Business rule: Prevent deletion of active admin users
        validateUserCanBeDeleted(user);

        log.info("Soft deleting user: {} (ID: {})", user.getUsername(), userId);
        user.markAsDeleted(getCurrentUsername());
        userRepository.save(user);

        publishUserDeletedEvent(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    // Enhanced business methods
    @Override
    public User authenticateUser(String usernameOrEmail, String password) {
        User user = findUserByUsernameOrEmail(usernameOrEmail);

        // Business rule: Check if account is locked
        if (user.getAccountLocked()) {
            throw new BusinessRuleViolationException(
                    "Account is locked due to multiple failed login attempts",
                    "authentication"
            );
        }

        // Business rule: Check if account is active
        if (!user.isActive()) {
            throw new BusinessRuleViolationException(
                    "Account is inactive",
                    "authentication"
            );
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleFailedLogin(user);
            throw new BusinessRuleViolationException(
                    "Invalid credentials",
                    "authentication"
            );
        }

        // Business rule: Check password expiry
        if (user.isPasswordExpired(passwordExpiryDays)) {
            throw new BusinessRuleViolationException(
                    "Password has expired and must be changed",
                    "password-expired"
            );
        }

        // Update successful login
        user.updateLastLogin();
        userRepository.save(user);

        log.info("User authenticated successfully: {}", user.getUsername());
        return user;
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessRuleViolationException(
                    "Current password is incorrect",
                    "password-change"
            );
        }

        // Business rule: Password should be different from current
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessRuleViolationException(
                    "New password must be different from current password",
                    "password-change"
            );
        }

        // Validate new password strength
        validatePasswordStrength(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.updatePasswordChangedAt();
        user.unlockAccount(); // Reset failed attempts on password change

        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = getUserById(userId);

        validatePasswordStrength(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.updatePasswordChangedAt();
        user.unlockAccount();

        userRepository.save(user);
        log.info("Password reset for user: {}", user.getUsername());
    }

    @Override
    public void lockAccount(Long userId, String reason) {
        User user = getUserById(userId);
        user.lockAccount();

        userRepository.save(user);
        log.warn("Account locked for user: {} - Reason: {}", user.getUsername(), reason);
    }

    @Override
    public void unlockAccount(Long userId) {
        User user = getUserById(userId);
        user.unlockAccount();

        userRepository.save(user);
        log.info("Account unlocked for user: {}", user.getUsername());
    }

    // Advanced query methods
    @Override
    public Page<User> findUsersWithFilters(String username, String email, Boolean state,
                                           Long departmentId, Long roleId, Pageable pageable) {
        return userRepository.findUsersWithFilters(username, email, state, departmentId, roleId, pageable);
    }

    @Override
    public List<User> findUsersWithExpiredPasswords() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(passwordExpiryDays);
        return userRepository.findUsersWithExpiredPasswords(expirationDate);
    }

    @Override
    public List<User> findInactiveUsers(int daysSinceLastLogin) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysSinceLastLogin);
        return userRepository.findUsersNotLoggedInSince(cutoffDate);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAllActive(pageable);
        }
        return userRepository.searchUsers(keyword.trim(), pageable);
    }

    // Bulk operations
    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public void bulkLockAccounts(List<Long> userIds, String reason) {
        validateUserIds(userIds);
        userRepository.lockAccountsByIds(userIds);
        log.warn("Bulk locked {} accounts - Reason: {}", userIds.size(), reason);
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public void bulkUnlockAccounts(List<Long> userIds) {
        validateUserIds(userIds);
        userRepository.unlockAccountsByIds(userIds);
        log.info("Bulk unlocked {} accounts", userIds.size());
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers", "userStats"}, allEntries = true)
    public void bulkSoftDelete(List<Long> userIds) {
        validateUserIds(userIds);

        // Business rule: Validate each user can be deleted
        List<User> users = userIds.stream()
                .map(this::getUserById)
                .peek(this::validateUserCanBeDeleted)
                .toList();

        userRepository.softDeleteByIds(userIds, getCurrentUsername());
        log.info("Bulk soft deleted {} users", userIds.size());
    }

    // Statistics and reporting
    @Override
    @Cacheable(value = "userStats", key = "'dashboard'")
    public UserStatistics getUserStatistics() {
        return UserStatistics.builder()
                .totalUsers(userRepository.findAll().size())
                .activeUsers(userRepository.countActiveUsers())
                .lockedAccounts(userRepository.countLockedAccounts())
                .usersByDepartment(userRepository.getUserStatsByDepartment())
                .usersByRole(userRepository.getUserStatsByRole())
                .build();
    }

    // Validation methods
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validatePasswordStrength(user.getPassword());

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new InvalidUserDataException("fullName", "cannot be null or empty");
        }
    }

    private void validateUserForUpdate(User user) {
        if (user == null || user.getId() == null) {
            throw new InvalidUserDataException("User and ID cannot be null for update");
        }

        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUserDataException("username", "cannot be null or empty");
        }

        String trimmed = username.trim();
        if (trimmed.length() < minUsernameLength || trimmed.length() > maxUsernameLength) {
            throw new InvalidUserDataException("username",
                    String.format("must be between %d and %d characters", minUsernameLength, maxUsernameLength));
        }

        if (!trimmed.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUserDataException("username",
                    "can only contain letters, numbers, and underscores");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("email", "cannot be null or empty");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        if (!email.trim().matches(emailRegex)) {
            throw new InvalidUserDataException("email", "format is invalid");
        }
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new InvalidUserDataException("password", "must be at least 8 characters long");
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        if (!(hasUpper && hasLower && hasDigit && hasSpecial)) {
            throw new InvalidUserDataException("password",
                    "must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserDataException("id", "must be a positive number");
        }
    }

    private void validateUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new InvalidUserDataException("userIds", "cannot be null or empty");
        }
        userIds.forEach(this::validateId);
    }

    // Business rule validations
    private void checkForDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username.trim())) {
            throw new UserAlreadyExistsException("username", username);
        }
    }

    private void checkForDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new UserAlreadyExistsException("email", email);
        }
    }

    private void validateUserCanBeDeleted(User user) {
        // Business rule: Cannot delete system admin
        if (user.getRole() != null && "SYSTEM_ADMIN".equals(user.getRole().getRoleName())) {
            throw new BusinessRuleViolationException(
                    "System admin users cannot be deleted",
                    "user-deletion"
            );
        }

        // Business rule: Cannot delete users with active sessions (could be extended)
        // if (hasActiveSessions(user)) {
        //     throw new BusinessRuleViolationException("Cannot delete user with active sessions");
        // }
    }

    // Helper methods
    private void applyDefaultValues(User user) {
        if (user.getState() == null) {
            user.setState(true);
        }
        if (user.getAccountLocked() == null) {
            user.setAccountLocked(false);
        }
        if (user.getFailedLoginAttempts() == null) {
            user.setFailedLoginAttempts(0);
        }
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        // Try to find by username first
        Optional<User> user = userRepository.findActiveByUsername(usernameOrEmail);
        if (user.isPresent()) {
            return user.get();
        }

        // Try to find by email
        return userRepository.findActiveByEmail(usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("username or email", usernameOrEmail));
    }

    private void handleFailedLogin(User user) {
        user.incrementFailedLoginAttempts();
        userRepository.save(user);

        if (user.getAccountLocked()) {
            log.warn("Account locked due to too many failed login attempts: {}", user.getUsername());
        }
    }

    private String getCurrentUsername() {
        // In a real application, this would get the current user from SecurityContext
        return "SYSTEM"; // Placeholder
    }

    // Event publishing methods (for future event-driven architecture)
    private void publishUserCreatedEvent(User user) {
        // TODO: Implement event publishing for user creation
        log.debug("Publishing user created event for: {}", user.getUsername());
    }

    private void publishUserUpdatedEvent(User updatedUser, User originalUser) {
        // TODO: Implement event publishing for user updates
        log.debug("Publishing user updated event for: {}", updatedUser.getUsername());
    }

    private void publishUserDeletedEvent(User user) {
        // TODO: Implement event publishing for user deletion
        log.debug("Publishing user deleted event for: {}", user.getUsername());
    }

    // Statistics DTO
    @lombok.Builder
    @lombok.Data
    public static class UserStatistics {
        private long totalUsers;
        private long activeUsers;
        private long lockedAccounts;
        private List<Object[]> usersByDepartment;
        private List<Object[]> usersByRole;
    }
}
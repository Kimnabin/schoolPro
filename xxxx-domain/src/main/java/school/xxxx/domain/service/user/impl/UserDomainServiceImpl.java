package school.xxxx.domain.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import school.xxxx.domain.exception.BusinessRuleViolationException;
import school.xxxx.domain.exception.user.InvalidUserDataException;
import school.xxxx.domain.exception.user.UserAlreadyExistsException;
import school.xxxx.domain.exception.user.UserNotFoundException;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.domain.service.user.UserDomainService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation của UserDomainService - KHÔNG có PasswordEncoder để tránh circular dependency
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    // ❌ LOẠI BỎ: private final PasswordEncoder passwordEncoder; // Gây circular dependency

    // Configurable business rules
    @Value("${app.business.user.min-username-length:3}")
    private int minUsernameLength;

    @Value("${app.business.user.max-username-length:50}")
    private int maxUsernameLength;

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
    @CacheEvict(value = {"users"}, allEntries = true)
    public User createUser(User user) {
        validateUserForCreation(user);

        // Business rule: Check for duplicates
        checkForDuplicateUsername(user.getUsername());
        checkForDuplicateEmail(user.getEmail());

        // Business rule: Default values
        applyDefaultValues(user);

        // ✅ PASSWORD ĐÃ ĐƯỢC HASH Ở APPLICATION LAYER (UserMapper)
        log.info("Creating new user: {}", user.getUsername());
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    @Override
    @CacheEvict(value = {"users"}, allEntries = true)
    public User updateUser(User user) {
        validateUserForUpdate(user);

        // Get existing user for comparison
        User existingUser = getUserById(user.getId());

        // Business rule: Check email uniqueness if changed
        if (!existingUser.getEmail().equals(user.getEmail())) {
            checkForDuplicateEmail(user.getEmail());
        }

        log.info("Updating user: {}", user.getUsername());
        User updatedUser = userRepository.save(user);

        return updatedUser;
    }

    @Override
    @CacheEvict(value = {"users"}, allEntries = true)
    public void deleteUser(Long userId) {
        validateId(userId);

        User user = getUserById(userId);

        // Business rule: Prevent deletion of active admin users
        validateUserCanBeDeleted(user);

        log.info("Soft deleting user: {} (ID: {})", user.getUsername(), userId);
        user.markAsDeleted(getCurrentUsername());
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return userRepository.existsById(id);
    }

    // ❌ LOẠI BỎ CÁC METHOD CẦN PASSWORD ENCODER
    // authenticateUser, changePassword, resetPassword sẽ được chuyển sang Application Layer

    // Private validation methods (giữ nguyên)
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        // ❌ LOẠI BỎ: validatePasswordStrength(user.getPassword());

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

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserDataException("id", "must be a positive number");
        }
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

    private String getCurrentUsername() {
        return "SYSTEM"; // Placeholder
    }
}
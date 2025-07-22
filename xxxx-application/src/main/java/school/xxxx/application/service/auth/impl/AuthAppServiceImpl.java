package school.xxxx.application.service.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.application.mapper.user.UserMapper;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.auth.AuthAppService;
import school.xxxx.domain.exception.BusinessRuleViolationException;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.user.UserDomainService;
import school.xxxx.domain.service.security.UserSecurityService;

/**
 * Implementation cho AuthAppService - xử lý authentication với PasswordEncoder
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthAppServiceImpl implements AuthAppService {

    private final UserDomainService userDomainService;
    private final UserSecurityService userSecurityService;
    private final PasswordEncoder passwordEncoder; // ✅ Có thể dùng PasswordEncoder ở Application Layer
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User authenticateUser(String usernameOrEmail, String password) {
        User user = userSecurityService.loadUserForAuthentication(usernameOrEmail);

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
            incrementFailedLoginAttempts(user.getId());
            throw new BusinessRuleViolationException(
                    "Invalid credentials",
                    "authentication"
            );
        }

        // Update successful login
        user.updateLastLogin();
        userDomainService.updateUser(user);

        log.info("User authenticated successfully: {}", user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userDomainService.getUserById(userId);

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

        // Hash new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.updatePasswordChangedAt();
        user.unlockAccount(); // Reset failed attempts on password change

        userDomainService.updateUser(user);
        log.info("Password changed for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userDomainService.getUserById(userId);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.updatePasswordChangedAt();
        user.unlockAccount();

        userDomainService.updateUser(user);
        log.info("Password reset for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void updateLastLogin(Long userId) {
        try {
            User user = userDomainService.getUserById(userId);
            user.updateLastLogin();
            userDomainService.updateUser(user);
            log.debug("Updated last login for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to update last login for user ID: {}", userId, e);
        }
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userDomainService.getUserById(userId);
        return userMapper.toDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userDomainService.getUserByUsername(username);
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void lockAccount(Long userId, String reason) {
        User user = userDomainService.getUserById(userId);
        user.lockAccount();
        userDomainService.updateUser(user);
        log.warn("Account locked for user ID: {} - Reason: {}", userId, reason);
    }

    @Override
    @Transactional
    public void unlockAccount(Long userId) {
        User user = userDomainService.getUserById(userId);
        user.unlockAccount();
        userDomainService.updateUser(user);
        log.info("Account unlocked for user ID: {}", userId);
    }

    @Override
    public boolean isAccountLocked(Long userId) {
        User user = userDomainService.getUserById(userId);
        return user.getAccountLocked();
    }

    @Override
    @Transactional
    public void incrementFailedLoginAttempts(Long userId) {
        try {
            User user = userDomainService.getUserById(userId);
            user.incrementFailedLoginAttempts();
            userDomainService.updateUser(user);
            log.debug("Incremented failed login attempts for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to increment login attempts for user ID: {}", userId, e);
        }
    }

    @Override
    @Transactional
    public void resetFailedLoginAttempts(Long userId) {
        try {
            User user = userDomainService.getUserById(userId);
            user.resetFailedLoginAttempts();
            userDomainService.updateUser(user);
            log.debug("Reset failed login attempts for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to reset login attempts for user ID: {}", userId, e);
        }
    }
}

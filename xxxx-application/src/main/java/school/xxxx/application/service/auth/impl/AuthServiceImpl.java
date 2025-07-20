package school.xxxx.application.service.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.application.mapper.user.UserMapper;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.auth.AuthService;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.user.UserDomainService;

/**
 * Authentication Service Implementation
 *
 * @author Senior Backend Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserDomainService userDomainService;
    private final UserMapper userMapper;

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
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        userDomainService.changePassword(userId, currentPassword, newPassword);
        log.info("Password changed for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        userDomainService.resetPassword(userId, newPassword);
        log.info("Password reset for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void lockAccount(Long userId, String reason) {
        userDomainService.lockAccount(userId, reason);
        log.warn("Account locked for user ID: {} - Reason: {}", userId, reason);
    }

    @Override
    @Transactional
    public void unlockAccount(Long userId) {
        userDomainService.unlockAccount(userId);
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
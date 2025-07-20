package school.xxxx.application.service.auth;

import school.xxxx.application.model.dto.user.response.UserResponseDTO;

/**
 * Authentication Service Interface
 *
 * @author Senior Backend Developer
 */
public interface AuthService {

    /**
     * Update user's last login timestamp
     */
    void updateLastLogin(Long userId);

    /**
     * Get user by ID for authentication purposes
     */
    UserResponseDTO getUserById(Long userId);

    /**
     * Get user by username for authentication purposes
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Change user password
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Reset user password (admin function)
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * Lock user account
     */
    void lockAccount(Long userId, String reason);

    /**
     * Unlock user account
     */
    void unlockAccount(Long userId);

    /**
     * Check if user account is locked
     */
    boolean isAccountLocked(Long userId);

    /**
     * Increment failed login attempts
     */
    void incrementFailedLoginAttempts(Long userId);

    /**
     * Reset failed login attempts
     */
    void resetFailedLoginAttempts(Long userId);
}
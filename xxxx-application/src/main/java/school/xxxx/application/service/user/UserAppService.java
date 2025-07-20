package school.xxxx.application.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.user.impl.UserDomainServiceImpl.UserStatistics;

import java.util.List;

/**
 * Enhanced UserDomainService interface with comprehensive business operations
 *
 * @author Senior Backend Developer
 */
public interface UserDomainService {

    // Basic CRUD operations
    User getUserById(Long userId);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long userId);

    // Existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long id);

    // Authentication and security operations
    /**
     * Authenticate user with username/email and password
     * @param usernameOrEmail username or email
     * @param password plain text password
     * @return authenticated user
     * @throws BusinessRuleViolationException if authentication fails
     */
    User authenticateUser(String usernameOrEmail, String password);

    /**
     * Change user password with current password verification
     * @param userId user ID
     * @param currentPassword current password for verification
     * @param newPassword new password
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Reset user password (admin operation)
     * @param userId user ID
     * @param newPassword new password
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * Lock user account
     * @param userId user ID
     * @param reason reason for locking
     */
    void lockAccount(Long userId, String reason);

    /**
     * Unlock user account
     * @param userId user ID
     */
    void unlockAccount(Long userId);

    // Advanced query operations
    /**
     * Find users with complex filters and pagination
     */
    Page<User> findUsersWithFilters(String username, String email, Boolean state,
                                    Long departmentId, Long roleId, Pageable pageable);

    /**
     * Find users with expired passwords
     */
    List<User> findUsersWithExpiredPasswords();

    /**
     * Find users who haven't logged in for specified days
     */
    List<User> findInactiveUsers(int daysSinceLastLogin);

    /**
     * Search users by keyword across multiple fields
     */
    Page<User> searchUsers(String keyword, Pageable pageable);

    // Bulk operations
    /**
     * Lock multiple accounts at once
     */
    void bulkLockAccounts(List<Long> userIds, String reason);

    /**
     * Unlock multiple accounts at once
     */
    void bulkUnlockAccounts(List<Long> userIds);

    /**
     * Soft delete multiple users at once
     */
    void bulkSoftDelete(List<Long> userIds);

    /**
     * Restore multiple soft-deleted users
     */
    void bulkRestore(List<Long> userIds);

    // Statistics and reporting
    /**
     * Get comprehensive user statistics
     */
    UserStatistics getUserStatistics();

    /**
     * Get user count by department
     */
    long countUsersByDepartment(Long departmentId);

    /**
     * Get user count by role
     */
    long countUsersByRole(Long roleId);
}
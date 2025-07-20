package school.xxxx.domain.repositoty.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.xxxx.domain.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced User Repository with advanced query methods and performance optimizations
 *
 * @author Senior Backend Developer
 */
public interface UserRepository {

    // Basic CRUD operations
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    void deleteById(Long id);

    // Existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(Long id);

    // Enhanced queries for business logic
    Optional<User> findActiveByUsername(String username);
    Optional<User> findActiveByEmail(String email);

    /**
     * Find users with filters and pagination
     */
    Page<User> findUsersWithFilters(String username, String email, Boolean state,
                                    Long departmentId, Long roleId, Pageable pageable);

    /**
     * Find users by role
     */
    List<User> findByRoleId(Long roleId);
    Page<User> findByRoleId(Long roleId, Pageable pageable);

    /**
     * Find users by department
     */
    List<User> findByDepartmentId(Long departmentId);
    Page<User> findByDepartmentId(Long departmentId, Pageable pageable);

    /**
     * Find active users only
     */
    List<User> findAllActive();
    Page<User> findAllActive(Pageable pageable);

    /**
     * Find locked accounts
     */
    List<User> findLockedAccounts();
    Page<User> findLockedAccounts(Pageable pageable);

    /**
     * Find users with expired passwords
     */
    List<User> findUsersWithExpiredPasswords(LocalDateTime passwordExpirationDate);

    /**
     * Find users created between dates
     */
    List<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find users by last login date
     */
    List<User> findUsersNotLoggedInSince(LocalDateTime date);

    /**
     * Search users by keyword (searches in username, email, fullName)
     */
    Page<User> searchUsers(String keyword, Pageable pageable);

    /**
     * Bulk operations
     */
    void softDeleteByIds(List<Long> ids, String deletedBy);
    void restoreByIds(List<Long> ids);
    void lockAccountsByIds(List<Long> ids);
    void unlockAccountsByIds(List<Long> ids);

    /**
     * Statistics queries
     */
    long countActiveUsers();
    long countUsersByRole(Long roleId);
    long countUsersByDepartment(Long departmentId);
    long countLockedAccounts();

    /**
     * Custom queries for reports
     */
    List<Object[]> getUserStatsByDepartment();
    List<Object[]> getUserStatsByRole();
    List<Object[]> getMonthlyUserRegistrations(int year);
}
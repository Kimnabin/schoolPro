package school.xxxx.infrastructure.persistence.mapper.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.domain.model.entity.User;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced JPA Mapper with optimized queries and performance hints
 *
 * @author Senior Backend Developer
 */
public interface UserJPAMapper extends JpaRepository<User, Long> {

    // Basic finders with soft delete awareness
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "user-queries")
    })
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<User> findByEmail(@Param("email") String email);

    // Active user queries
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.state = true AND u.accountLocked = false AND u.deletedAt IS NULL")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.state = true AND u.accountLocked = false AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.state = true AND u.accountLocked = false AND u.deletedAt IS NULL")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.state = true AND u.accountLocked = false AND u.deletedAt IS NULL")
    Page<User> findAllActive(Pageable pageable);

    // Complex filtered search with dynamic conditions
    @Query("""
        SELECT u FROM User u 
        LEFT JOIN FETCH u.role r 
        LEFT JOIN FETCH u.department d 
        WHERE u.deletedAt IS NULL 
        AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) 
        AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) 
        AND (:state IS NULL OR u.state = :state) 
        AND (:departmentId IS NULL OR u.department.id = :departmentId) 
        AND (:roleId IS NULL OR u.role.id = :roleId)
        """)
    Page<User> findUsersWithFilters(
            @Param("username") String username,
            @Param("email") String email,
            @Param("state") Boolean state,
            @Param("departmentId") Long departmentId,
            @Param("roleId") Long roleId,
            Pageable pageable
    );

    // Role-based queries
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    Page<User> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    // Department-based queries
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.department.id = :departmentId AND u.deletedAt IS NULL")
    List<User> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.department.id = :departmentId AND u.deletedAt IS NULL")
    Page<User> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    // Security-related queries
    @Query("SELECT u FROM User u WHERE u.accountLocked = true AND u.deletedAt IS NULL")
    List<User> findLockedAccounts();

    @Query("SELECT u FROM User u WHERE u.accountLocked = true AND u.deletedAt IS NULL")
    Page<User> findLockedAccounts(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.passwordChangedAt < :expirationDate OR u.passwordChangedAt IS NULL AND u.deletedAt IS NULL")
    List<User> findUsersWithExpiredPasswords(@Param("expirationDate") LocalDateTime expirationDate);

    // Date-based queries
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
    Page<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date OR u.lastLoginAt IS NULL AND u.deletedAt IS NULL")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);

    // Full-text search across multiple fields
    @Query("""
        SELECT u FROM User u 
        LEFT JOIN FETCH u.role r 
        LEFT JOIN FETCH u.department d 
        WHERE u.deletedAt IS NULL 
        AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    // Bulk operations with optimized queries
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.deletedAt = :deletedAt, u.deletedBy = :deletedBy WHERE u.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids, @Param("deletedAt") LocalDateTime deletedAt, @Param("deletedBy") String deletedBy);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.deletedAt = NULL, u.deletedBy = NULL WHERE u.id IN :ids")
    void restoreByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountLocked = true WHERE u.id IN :ids")
    void lockAccountsByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.accountLocked = false, u.failedLoginAttempts = 0 WHERE u.id IN :ids")
    void unlockAccountsByIds(@Param("ids") List<Long> ids);

    // Statistical queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.state = true AND u.accountLocked = false AND u.deletedAt IS NULL")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    long countUsersByRole(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.department.id = :departmentId AND u.deletedAt IS NULL")
    long countUsersByDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.accountLocked = true AND u.deletedAt IS NULL")
    long countLockedAccounts();

    // Advanced reporting queries
    @Query("""
        SELECT d.nameDepartment, COUNT(u), 
               SUM(CASE WHEN u.state = true THEN 1 ELSE 0 END) as activeCount,
               SUM(CASE WHEN u.accountLocked = true THEN 1 ELSE 0 END) as lockedCount
        FROM User u 
        LEFT JOIN u.department d 
        WHERE u.deletedAt IS NULL 
        GROUP BY d.id, d.nameDepartment
        """)
    List<Object[]> getUserStatsByDepartment();

    @Query("""
        SELECT r.roleName, COUNT(u), 
               SUM(CASE WHEN u.state = true THEN 1 ELSE 0 END) as activeCount
        FROM User u 
        LEFT JOIN u.role r 
        WHERE u.deletedAt IS NULL 
        GROUP BY r.id, r.roleName
        """)
    List<Object[]> getUserStatsByRole();

    @Query("""
        SELECT MONTH(u.createdAt) as month, COUNT(u) as userCount 
        FROM User u 
        WHERE YEAR(u.createdAt) = :year AND u.deletedAt IS NULL 
        GROUP BY MONTH(u.createdAt) 
        ORDER BY month
        """)
    List<Object[]> getMonthlyUserRegistrations(@Param("year") int year);

    // Existence checks with soft delete awareness
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    // Override default findAll to exclude soft deleted
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    Page<User> findAll(Pageable pageable);
}
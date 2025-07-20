package school.xxxx.infrastructure.persistence.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.repositoty.user.UserRepository;
import school.xxxx.infrastructure.persistence.mapper.user.UserJPAMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced UserRepository implementation with caching and performance optimizations
 *
 * @author Senior Backend Developer
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJPAMapper userJPAMapper;

    // Basic CRUD with caching
    @Override
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public Optional<User> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userJPAMapper.findById(id);
    }

    @Override
    @Cacheable(value = "users", key = "'username:' + #username", unless = "#result == null")
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userJPAMapper.findByUsername(username);
    }

    @Override
    @Cacheable(value = "users", key = "'email:' + #email", unless = "#result == null")
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userJPAMapper.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        return userJPAMapper.findAll();
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User save(User user) {
        log.debug("Saving user: {}", user.getUsername());
        User savedUser = userJPAMapper.save(user);
        log.info("User saved successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteById(Long id) {
        log.debug("Deleting user by id: {}", id);
        userJPAMapper.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    // Existence checks
    @Override
    public boolean existsByUsername(String username) {
        return userJPAMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJPAMapper.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userJPAMapper.existsById(id);
    }

    // Enhanced business queries
    @Override
    @Cacheable(value = "activeUsers", key = "'username:' + #username", unless = "#result == null")
    public Optional<User> findActiveByUsername(String username) {
        log.debug("Finding active user by username: {}", username);
        return userJPAMapper.findActiveByUsername(username);
    }

    @Override
    @Cacheable(value = "activeUsers", key = "'email:' + #email", unless = "#result == null")
    public Optional<User> findActiveByEmail(String email) {
        log.debug("Finding active user by email: {}", email);
        return userJPAMapper.findActiveByEmail(email);
    }

    @Override
    public Page<User> findUsersWithFilters(String username, String email, Boolean state,
                                           Long departmentId, Long roleId, Pageable pageable) {
        log.debug("Finding users with filters - username: {}, email: {}, state: {}, dept: {}, role: {}, page: {}",
                username, email, state, departmentId, roleId, pageable.getPageNumber());

        return userJPAMapper.findUsersWithFilters(username, email, state, departmentId, roleId, pageable);
    }

    @Override
    public List<User> findByRoleId(Long roleId) {
        log.debug("Finding users by role id: {}", roleId);
        return userJPAMapper.findByRoleId(roleId);
    }

    @Override
    public Page<User> findByRoleId(Long roleId, Pageable pageable) {
        log.debug("Finding users by role id: {} with pagination", roleId);
        return userJPAMapper.findByRoleId(roleId, pageable);
    }

    @Override
    public List<User> findByDepartmentId(Long departmentId) {
        log.debug("Finding users by department id: {}", departmentId);
        return userJPAMapper.findByDepartmentId(departmentId);
    }

    @Override
    public Page<User> findByDepartmentId(Long departmentId, Pageable pageable) {
        log.debug("Finding users by department id: {} with pagination", departmentId);
        return userJPAMapper.findByDepartmentId(departmentId, pageable);
    }

    @Override
    @Cacheable(value = "activeUsers", key = "'all'")
    public List<User> findAllActive() {
        log.debug("Finding all active users");
        return userJPAMapper.findAllActive();
    }

    @Override
    public Page<User> findAllActive(Pageable pageable) {
        log.debug("Finding all active users with pagination");
        return userJPAMapper.findAllActive(pageable);
    }

    @Override
    public List<User> findLockedAccounts() {
        log.debug("Finding all locked accounts");
        return userJPAMapper.findLockedAccounts();
    }

    @Override
    public Page<User> findLockedAccounts(Pageable pageable) {
        log.debug("Finding locked accounts with pagination");
        return userJPAMapper.findLockedAccounts(pageable);
    }

    @Override
    public List<User> findUsersWithExpiredPasswords(LocalDateTime passwordExpirationDate) {
        log.debug("Finding users with expired passwords before: {}", passwordExpirationDate);
        return userJPAMapper.findUsersWithExpiredPasswords(passwordExpirationDate);
    }

    @Override
    public List<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding users created between {} and {}", startDate, endDate);
        return userJPAMapper.findUsersCreatedBetween(startDate, endDate);
    }

    @Override
    public Page<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding users created between {} and {} with pagination", startDate, endDate);
        return userJPAMapper.findUsersCreatedBetween(startDate, endDate, pageable);
    }

    @Override
    public List<User> findUsersNotLoggedInSince(LocalDateTime date) {
        log.debug("Finding users not logged in since: {}", date);
        return userJPAMapper.findUsersNotLoggedInSince(date);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        log.debug("Searching users with keyword: {} and pagination", keyword);
        return userJPAMapper.searchUsers(keyword, pageable);
    }

    // Bulk operations
    @Override
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public void softDeleteByIds(List<Long> ids, String deletedBy) {
        log.debug("Soft deleting users with ids: {} by user: {}", ids, deletedBy);
        userJPAMapper.softDeleteByIds(ids, LocalDateTime.now(), deletedBy);
        log.info("Soft deleted {} users", ids.size());
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public void restoreByIds(List<Long> ids) {
        log.debug("Restoring users with ids: {}", ids);
        userJPAMapper.restoreByIds(ids);
        log.info("Restored {} users", ids.size());
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public void lockAccountsByIds(List<Long> ids) {
        log.debug("Locking accounts with ids: {}", ids);
        userJPAMapper.lockAccountsByIds(ids);
        log.info("Locked {} accounts", ids.size());
    }

    @Override
    @CacheEvict(value = {"users", "activeUsers"}, allEntries = true)
    public void unlockAccountsByIds(List<Long> ids) {
        log.debug("Unlocking accounts with ids: {}", ids);
        userJPAMapper.unlockAccountsByIds(ids);
        log.info("Unlocked {} accounts", ids.size());
    }

    // Statistics
    @Override
    @Cacheable(value = "userStats", key = "'activeCount'")
    public long countActiveUsers() {
        return userJPAMapper.countActiveUsers();
    }

    @Override
    @Cacheable(value = "userStats", key = "'roleCount:' + #roleId")
    public long countUsersByRole(Long roleId) {
        return userJPAMapper.countUsersByRole(roleId);
    }

    @Override
    @Cacheable(value = "userStats", key = "'deptCount:' + #departmentId")
    public long countUsersByDepartment(Long departmentId) {
        return userJPAMapper.countUsersByDepartment(departmentId);
    }

    @Override
    public long countLockedAccounts() {
        return userJPAMapper.countLockedAccounts();
    }

    // Reports
    @Override
    @Cacheable(value = "userReports", key = "'departmentStats'")
    public List<Object[]> getUserStatsByDepartment() {
        log.debug("Getting user statistics by department");
        return userJPAMapper.getUserStatsByDepartment();
    }

    @Override
    @Cacheable(value = "userReports", key = "'roleStats'")
    public List<Object[]> getUserStatsByRole() {
        log.debug("Getting user statistics by role");
        return userJPAMapper.getUserStatsByRole();
    }

    @Override
    @Cacheable(value = "userReports", key = "'monthlyRegistrations:' + #year")
    public List<Object[]> getMonthlyUserRegistrations(int year) {
        log.debug("Getting monthly user registrations for year: {}", year);
        return userJPAMapper.getMonthlyUserRegistrations(year);
    }
}
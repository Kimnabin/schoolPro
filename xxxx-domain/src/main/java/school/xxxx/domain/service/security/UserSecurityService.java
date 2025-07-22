package school.xxxx.domain.service.security;

import school.xxxx.domain.model.entity.User;

/**
 * Domain Service chuyên cho Security - tách riêng để tránh circular dependency
 */
public interface UserSecurityService {

    /**
     * Load user by username hoặc email cho authentication
     */
    User loadUserForAuthentication(String usernameOrEmail);

    /**
     * Load user by ID cho JWT authentication
     */
    User loadUserById(Long userId);
}
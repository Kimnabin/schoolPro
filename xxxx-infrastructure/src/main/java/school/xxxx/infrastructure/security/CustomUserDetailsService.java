package school.xxxx.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.security.UserSecurityService;

/**
 * Custom UserDetailsService - CHỈ dùng UserSecurityService (KHÔNG dùng UserDomainService)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserSecurityService userSecurityService; // ✅ Chỉ dùng Security Service

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user details for: {}", usernameOrEmail);

        try {
            User user = userSecurityService.loadUserForAuthentication(usernameOrEmail);
            return UserPrincipal.create(user);
        } catch (Exception e) {
            log.error("Error loading user details for: {}", usernameOrEmail, e);
            throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
        }
    }

    /**
     * Load user by ID (for JWT authentication)
     */
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user details for ID: {}", id);

        try {
            User user = userSecurityService.loadUserById(id);
            return UserPrincipal.create(user);
        } catch (Exception e) {
            log.error("Error loading user details for ID: {}", id, e);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}
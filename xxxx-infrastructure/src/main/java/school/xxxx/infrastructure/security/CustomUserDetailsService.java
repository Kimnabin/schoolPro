package school.xxxx.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.xxxx.domain.model.entity.User;
import school.xxxx.domain.service.user.UserDomainService;

/**
 * Custom UserDetailsService implementation for Spring Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDomainService userDomainService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user details for: {}", usernameOrEmail);

        try {
            User user;

            // Try to find by username first
            if (userDomainService.existsByUsername(usernameOrEmail)) {
                user = userDomainService.getUserByUsername(usernameOrEmail);
            } else if (userDomainService.existsByEmail(usernameOrEmail)) {
                user = userDomainService.getUserByEmail(usernameOrEmail);
            } else {
                throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
            }

            return UserPrincipal.create(user);

        } catch (Exception e) {
            log.error("Error loading user details for: {}", usernameOrEmail, e);
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }
    }

    /**
     * Load user by ID (useful for JWT authentication)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        log.debug("Loading user details for ID: {}", id);

        try {
            User user = userDomainService.getUserById(id);
            return UserPrincipal.create(user);
        } catch (Exception e) {
            log.error("Error loading user details for ID: {}", id, e);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
    }
}
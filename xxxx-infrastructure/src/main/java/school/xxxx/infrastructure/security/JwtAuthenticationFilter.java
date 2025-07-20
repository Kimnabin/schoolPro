package school.xxxx.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import school.xxxx.infrastructure.security.JwtTokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter to validate JWT tokens on each request
 *
 * @author Senior Backend Developer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);

                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails != null) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("User '{}' authenticated successfully", username);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            // Clear security context on error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Skip JWT filter for certain endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip authentication for public endpoints
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/home/") ||
                path.startsWith("/actuator/") ||
                path.equals("/api/v1/test/") ||
                path.startsWith("/h2-console/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".ico");
    }
}

/**
 * Custom UserDetailsService implementation
 */
@Component
@RequiredArgsConstructor
@Slf4j
class CustomUserDetailsService implements UserDetailsService {

    private final school.xxxx.domain.service.user.UserDomainService userDomainService;

    @Override
    public UserDetails loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        try {
            school.xxxx.domain.model.entity.User user = userDomainService.getUserByUsername(username);

            return UserPrincipal.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .enabled(user.getState())
                    .accountNonLocked(!user.getAccountLocked())
                    .authorities(mapRolesToAuthorities(user))
                    .build();

        } catch (Exception e) {
            log.error("User not found with username: {}", username);
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "User not found with username: " + username);
        }
    }

    /**
     * Map user roles to Spring Security authorities
     */
    private List<SimpleGrantedAuthority> mapRolesToAuthorities(school.xxxx.domain.model.entity.User user) {
        List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();

        // Add role-based authority
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));

            // Add specific permissions based on role
            switch (user.getRole().getRoleName()) {
                case "ADMIN":
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_USER_MANAGE"));
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_MANAGE"));
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_REPORT_VIEW"));
                    break;
                case "MANAGER":
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_MANAGE"));
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_REPORT_VIEW"));
                    break;
                case "USER":
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_VIEW"));
                    break;
                default:
                    authorities.add(new SimpleGrantedAuthority("PERMISSION_BASIC"));
            }
        } else {
            // Default role if no role assigned
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            authorities.add(new SimpleGrantedAuthority("PERMISSION_BASIC"));
        }

        log.debug("User {} has authorities: {}", user.getUsername(),
                authorities.stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

        return authorities;
    }
}
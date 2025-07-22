package school.xxxx.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import school.xxxx.domain.model.entity.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserPrincipal implementation for Spring Security
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean enabled;
    private boolean accountNonLocked;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Create UserPrincipal from User entity
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = mapRolesToAuthorities(user);

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(user.getState())
                .accountNonLocked(!user.getAccountLocked())
                .authorities(authorities)
                .build();
    }

    /**
     * Map user roles to Spring Security authorities
     */
    private static List<GrantedAuthority> mapRolesToAuthorities(User user) {
        if (user.getRole() == null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        String roleName = user.getRole().getRoleName();
        List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();

        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        // Add permissions based on role
        switch (roleName.toUpperCase()) {
            case "ADMIN":
            case "SYSTEM_ADMIN":
                authorities.add(new SimpleGrantedAuthority("PERMISSION_USER_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_REPORT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_SYSTEM_CONFIG"));
                break;
            case "MANAGER":
                authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_REPORT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_USER_VIEW"));
                break;
            case "USER":
                authorities.add(new SimpleGrantedAuthority("PERMISSION_ASSET_VIEW"));
                authorities.add(new SimpleGrantedAuthority("PERMISSION_PROFILE_EDIT"));
                break;
            default:
                authorities.add(new SimpleGrantedAuthority("PERMISSION_BASIC"));
        }

        return authorities.stream()
                .map(auth -> (GrantedAuthority) auth)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
}
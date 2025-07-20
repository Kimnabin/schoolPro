package school.xxxx.controller.http;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import school.xxxx.application.model.dto.auth.request.LoginRequestDTO;
import school.xxxx.application.model.dto.auth.request.RefreshTokenRequestDTO;
import school.xxxx.application.model.dto.auth.request.ChangePasswordRequestDTO;
import school.xxxx.application.model.dto.auth.response.AuthResponseDTO;
import school.xxxx.application.model.dto.user.response.UserResponseDTO;
import school.xxxx.application.service.auth.AuthService;
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;
import school.xxxx.infrastructure.security.JwtTokenProvider;
import school.xxxx.infrastructure.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller for user login, logout, token refresh, etc.
 *
 * @author Senior Backend Developer
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    /**
     * User login endpoint
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ResultMessage<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT tokens
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Create response
            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationDateFromToken(accessToken).getTime())
                    .user(mapToUserResponse(userPrincipal))
                    .loginTime(LocalDateTime.now())
                    .build();

            // Update user last login
            authService.updateLastLogin(userPrincipal.getId());

            log.info("User logged in successfully: {}", userPrincipal.getUsername());
            return ResponseEntity.ok(ResultUtil.zdata(authResponse));

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {} - {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResultUtil.error(ResultCode.USER_PASSWORD_ERROR.code(), "Invalid credentials"));
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR.code(), "Login failed"));
        }
    }

    /**
     * Refresh token endpoint
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResultMessage<AuthResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            if (!tokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResultUtil.error(ResultCode.USER_SESSION_EXPIRED.code(), "Invalid refresh token"));
            }

            String username = tokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = tokenProvider.generateTokenFromUsername(username);

            // Get user details for response
            UserResponseDTO user = authService.getUserByUsername(username);

            AuthResponseDTO authResponse = AuthResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Keep the same refresh token
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationDateFromToken(newAccessToken).getTime())
                    .user(user)
                    .loginTime(LocalDateTime.now())
                    .build();

            log.info("Token refreshed for user: {}", username);
            return ResponseEntity.ok(ResultUtil.zdata(authResponse));

        } catch (Exception e) {
            log.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResultUtil.error(ResultCode.USER_SESSION_EXPIRED.code(), "Token refresh failed"));
        }
    }

    /**
     * Get current user profile
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ResultMessage<UserResponseDTO>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResultUtil.error(ResultCode.USER_AUTH_ERROR.code(), "Not authenticated"));
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            UserResponseDTO user = authService.getUserById(userPrincipal.getId());

            return ResponseEntity.ok(ResultUtil.zdata(user));

        } catch (Exception e) {
            log.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR.code(), "Failed to get user profile"));
        }
    }

    /**
     * Change password
     * POST /api/v1/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ResultMessage<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest,
            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResultUtil.error(ResultCode.USER_AUTH_ERROR.code(), "Not authenticated"));
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            authService.changePassword(
                    userPrincipal.getId(),
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword()
            );

            log.info("Password changed successfully for user: {}", userPrincipal.getUsername());
            return ResponseEntity.ok(ResultUtil.zdata("Password changed successfully"));

        } catch (Exception e) {
            log.error("Password change error for user: {}",
                    authentication != null ? authentication.getName() : "unknown", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResultUtil.error(ResultCode.USER_PASSWORD_ERROR.code(), e.getMessage()));
        }
    }

    /**
     * Logout endpoint
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ResultMessage<String>> logout(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                log.info("User logged out: {}", userPrincipal.getUsername());

                // In a production system, you might want to:
                // 1. Add the JWT to a blacklist
                // 2. Clear any cached user sessions
                // 3. Log the logout event

                SecurityContextHolder.clearContext();
            }

            return ResponseEntity.ok(ResultUtil.zdata("Logged out successfully"));

        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultUtil.error(ResultCode.ERROR.code(), "Logout failed"));
        }
    }

    /**
     * Check token validity
     * GET /api/v1/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<ResultMessage<Map<String, Object>>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = tokenProvider.validateToken(token);

                Map<String, Object> response = new HashMap<>();
                response.put("valid", isValid);

                if (isValid) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    response.put("username", username);
                    response.put("expiresAt", tokenProvider.getExpirationDateFromToken(token));
                }

                return ResponseEntity.ok(ResultUtil.zdata(response));
            }

            Map<String, Object> response = Map.of("valid", false, "message", "No token provided");
            return ResponseEntity.ok(ResultUtil.zdata(response));

        } catch (Exception e) {
            log.error("Token validation error", e);
            Map<String, Object> response = Map.of("valid", false, "message", "Token validation failed");
            return ResponseEntity.ok(ResultUtil.zdata(response));
        }
    }

    /**
     * Get authentication info (useful for debugging)
     * GET /api/v1/auth/info
     */
    @GetMapping("/info")
    public ResponseEntity<ResultMessage<Map<String, Object>>> getAuthInfo(Authentication authentication) {
        Map<String, Object> authInfo = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            authInfo.put("authenticated", true);
            authInfo.put("username", userPrincipal.getUsername());
            authInfo.put("authorities", authentication.getAuthorities());
            authInfo.put("accountNonLocked", userPrincipal.isAccountNonLocked());
            authInfo.put("enabled", userPrincipal.isEnabled());
        } else {
            authInfo.put("authenticated", false);
        }

        return ResponseEntity.ok(ResultUtil.zdata(authInfo));
    }

    /**
     * Helper method to map UserPrincipal to UserResponseDTO
     */
    private UserResponseDTO mapToUserResponse(UserPrincipal userPrincipal) {
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setId(userPrincipal.getId());
        userResponse.setFullName(userPrincipal.getUsername()); // You might want to get actual full name
        userResponse.setEmail(userPrincipal.getEmail());
        return userResponse;
    }
}
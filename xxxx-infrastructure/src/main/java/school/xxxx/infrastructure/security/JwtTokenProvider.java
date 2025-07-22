package school.xxxx.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating and validating JWT tokens
 *
 * @author Senior Backend Developer
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${app.security.jwt.secret}") String jwtSecret,
            @Value("${app.security.jwt.expiration}") long jwtExpirationMs,
            @Value("${app.security.jwt.refresh-expiration}") long refreshExpirationMs) {

        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs * 1000; // Convert to milliseconds
        this.refreshExpirationMs = refreshExpirationMs * 1000;
    }

    /**
     * Generate JWT token from authentication
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserPrincipal(userPrincipal, jwtExpirationMs);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserPrincipal(userPrincipal, refreshExpirationMs);
    }

    /**
     * Generate token from UserPrincipal
     */
    private String generateTokenFromUserPrincipal(UserPrincipal userPrincipal, long expirationMs) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(expirationMs, ChronoUnit.MILLIS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId());
        claims.put("username", userPrincipal.getUsername());
        claims.put("email", userPrincipal.getEmail());
        claims.put("authorities", userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("isAccountLocked", userPrincipal.isAccountNonLocked());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * Get all claims from JWT token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * Generate token from username (for refresh scenarios)
     */
    public String generateTokenFromUsername(String username) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
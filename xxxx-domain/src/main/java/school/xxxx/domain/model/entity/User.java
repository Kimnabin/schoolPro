package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Optimized User entity with proper indexing, soft delete, and performance optimizations
 *
 * @author Senior Backend Developer
 */
@Entity
@Table(name = "user", indexes = {
        @Index(name = "idx_user_username", columnList = "username", unique = true),
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_state", columnList = "state"),
        @Index(name = "idx_user_deleted_at", columnList = "deleted_at"),
        @Index(name = "idx_user_created_at", columnList = "created_at"),
        @Index(name = "idx_user_department", columnList = "department_id"),
        @Index(name = "idx_user_role", columnList = "role_id")
})
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW(), deleted_by = ? WHERE id = ? AND version = ?")
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
@BatchSize(size = 20) // Optimize N+1 queries
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "state", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean state = true;

    /**
     * Account locking fields for security
     */
    @Column(name = "account_locked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean accountLocked = false;

    @Column(name = "failed_login_attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private java.time.LocalDateTime passwordChangedAt;

    /**
     * Eager loading for role as it's frequently accessed
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    /**
     * Lazy loading for department as it's not always needed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_user_department"))
    private Department department;

    // Business methods
    public boolean isActive() {
        return state && !accountLocked && !isDeleted();
    }

    public void lockAccount() {
        this.accountLocked = true;
    }

    public void unlockAccount() {
        this.accountLocked = false;
        this.failedLoginAttempts = 0;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) { // Configurable threshold
            lockAccount();
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void updateLastLogin() {
        this.lastLoginAt = java.time.LocalDateTime.now();
        resetFailedLoginAttempts();
    }

    public void updatePasswordChangedAt() {
        this.passwordChangedAt = java.time.LocalDateTime.now();
    }

    public boolean isPasswordExpired(int passwordExpirationDays) {
        if (passwordChangedAt == null) {
            return true; // Force password change if never set
        }
        return passwordChangedAt.plusDays(passwordExpirationDays)
                .isBefore(java.time.LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", state=" + state +
                ", accountLocked=" + accountLocked +
                '}';
    }
}
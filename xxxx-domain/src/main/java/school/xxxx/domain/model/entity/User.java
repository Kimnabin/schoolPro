package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
// table luu tru thong tin nguoi dung trong he thong, bao gom thong tin dang nhap, thong tin ca nhan
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20, unique = true)
    private Integer phoneNumber;

    @Column(name = "address", length = 255, nullable = true)
    private String address;

    @Column(name = "state", nullable = false, columnDefinition = "boolean default false")
    private Boolean state;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private department department;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private role role;

}

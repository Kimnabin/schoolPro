package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
// Table danh sach cac vai tro trong he thong, phuc vu viec phan quyen
public class Role {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", length = 50, unique = true)
    private String roleName;
}

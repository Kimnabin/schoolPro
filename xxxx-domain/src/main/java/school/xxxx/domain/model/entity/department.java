package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "department")
// vi tri cua phong ban, lop hoc, khoa noi luu giu, su dung tai san
public class department {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String nameDepartment;

    @Column(name = "location", length = 255, nullable = false, unique = true)
    private String location;
}

package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "asset_type")
// Table liet ke loai tai san
public class assetType {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String assetTypeName;

    @Column(name = "description", length = 255, nullable = true)
    private String description;



}

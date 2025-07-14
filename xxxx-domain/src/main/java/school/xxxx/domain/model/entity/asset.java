package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "asset")
// Table thong tin tung tai san cu the
public class asset {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 100, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "purchase_date", nullable = false)
    private Date purchaseDate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description", length = 500, nullable = true)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "asset_type_id")
    private assetType assetType_id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private department department_id;

}

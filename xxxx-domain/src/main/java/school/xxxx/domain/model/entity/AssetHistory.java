package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asset_history")
// Table ghi lai lich su chuyen giao, thay doi tai san
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Column(name = "action", nullable = false, length = 30)
    private String action;

    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "old_department_id")
    private Department oldDepartment;

    @ManyToOne
    @JoinColumn(name = "new_department_id")
    private Department newDepartment;

}

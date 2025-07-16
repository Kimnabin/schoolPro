package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "inventory_check_session")
// Table kiem ke tai san
public class InventoryCheckSession {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_name", nullable = false, length = 100)
    private String sessionName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @Column(name = "status", nullable = false)
    private int status; // 0: Not Started, 1: In Progress, 2: Completed

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Nguoi tao phien kiem ke

}

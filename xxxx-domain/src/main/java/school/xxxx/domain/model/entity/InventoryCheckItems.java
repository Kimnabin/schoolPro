package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "inventory_check_items")
// Table chi tiet kiem ke tung tai san, trang thiet bi, vat tu trong moi doi kiem ke
public class InventoryCheckItems {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checked_at", nullable = false)
    private Date checkedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "actual_location", nullable = false, length = 100)
    private String actualLocation;

    @Column(name = "note", nullable = false, length = 100)
    private String note;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private InventoryCheckSession inventoryCheckSession_id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset_id;

    @ManyToOne
    @JoinColumn(name = "checked_by", nullable = false)
    private User user_id;

}

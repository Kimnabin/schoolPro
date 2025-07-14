package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "maintenance_log")
// Tabel luu tru thong tin bao tri, sua chua, bao tri tai san
public class maintenanceLog {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "maintenance_date", nullable = false)
    private Date mainternanceDate;

    @Column(name = "issue_description", nullable = false, length = 500)
    private String issueDescription;

    @Column(name = "solution", nullable = false, length = 500)
    private String solution;

    @Column(name = "cost", nullable = false)
    private double cost;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private asset asset; // lien ket den tai san duoc bao tri

}

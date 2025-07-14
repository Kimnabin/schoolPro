package school.xxxx.domain.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "asset_image")
// Table luu lai hinh anh tai san
public class assetImage {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    private String imageUrl;

    private String imageName;

    private String caption;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private asset asset_id; // lien ket den tai san lien quan

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User user_id; // lien ket den nguoi dung da tai len hinh anh
}

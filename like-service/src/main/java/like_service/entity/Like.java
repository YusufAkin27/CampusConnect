package like_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Beğeni entity sınıfı.
 * Bir kullanıcının belirli bir hedef içeriği beğendiğini temsil eder.
 */
@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_target",
                        columnNames = {"user_id", "target_id", "target_type"}
                )
        },
        indexes = {
                @Index(name = "idx_likes_user_id", columnList = "user_id"),
                @Index(name = "idx_likes_target_id", columnList = "target_id"),
                @Index(name = "idx_likes_target_type", columnList = "target_type"),
                @Index(name = "idx_likes_status", columnList = "status"),
                @Index(name = "idx_likes_created_at", columnList = "created_at"),
                @Index(name = "idx_likes_target_id_type_status", columnList = "target_id, target_type, status"),
                @Index(name = "idx_likes_user_id_status", columnList = "user_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private LikeTargetType targetType;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private LikeStatus status = LikeStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

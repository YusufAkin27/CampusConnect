package notification_service.entity;

import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_receiver", columnList = "receiver_user_id"),
        @Index(name = "idx_notifications_receiver_read", columnList = "receiver_user_id, read"),
        @Index(name = "idx_notifications_type", columnList = "type"),
        @Index(name = "idx_notifications_priority", columnList = "priority"),
        @Index(name = "idx_notifications_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_user_id", nullable = false)
    private Long receiverUserId;

    @Column(name = "sender_user_id")
    private Long senderUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType;

    @Column(nullable = false, length = 120)
    private String targetId;

    @Column(length = 400)
    private String actionUrl;

    @Column(length = 400)
    private String imageUrl;

    @Column(nullable = false)
    private boolean read;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private boolean delivered;

    private LocalDateTime deliveredAt;

    private LocalDateTime expiresAt;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

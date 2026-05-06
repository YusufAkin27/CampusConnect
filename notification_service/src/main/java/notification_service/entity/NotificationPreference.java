package notification_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "notification_preferences",
    indexes = {
        @Index(name = "uk_notification_preferences_user", columnList = "user_id", unique = true)
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private boolean inAppEnabled;

    @Column(nullable = false)
    private boolean pushEnabled;

    @Column(nullable = false)
    private boolean emailEnabled;

    @Column(nullable = false)
    private boolean chatNotificationsEnabled;

    @Column(nullable = false)
    private boolean eventNotificationsEnabled;

    @Column(nullable = false)
    private boolean friendNotificationsEnabled;

    @Column(nullable = false)
    private boolean postNotificationsEnabled;

    @Column(nullable = false)
    private boolean marketingNotificationsEnabled;

    @Column(nullable = false)
    private boolean quietHoursEnabled;

    private LocalTime quietHoursStart;

    private LocalTime quietHoursEnd;

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

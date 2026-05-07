package admin_service.entity;

import admin_service.enums.BanType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Record of a user ban action performed by an admin.
 * Tracks ban history, type, reason, and expiration.
 */
@Entity
@Table(name = "user_ban_records", indexes = {
        @Index(name = "idx_ban_user_id", columnList = "userId"),
        @Index(name = "idx_ban_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long bannedByAdminId;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BanType banType;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
}

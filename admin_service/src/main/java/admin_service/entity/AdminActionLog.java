package admin_service.entity;

import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Audit log entity recording every critical administrative action
 * performed in the system.
 */
@Entity
@Table(name = "admin_action_logs", indexes = {
        @Index(name = "idx_action_admin_id", columnList = "adminId"),
        @Index(name = "idx_action_type", columnList = "actionType"),
        @Index(name = "idx_action_target", columnList = "targetType,targetId"),
        @Index(name = "idx_action_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TargetType targetType;

    private Long targetId;

    @Column(length = 1000)
    private String description;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

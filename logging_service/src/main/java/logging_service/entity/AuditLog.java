package logging_service.entity;

import jakarta.persistence.*;
import logging_service.enums.AuditAction;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Audit log entity.
 * Tracks important user-initiated actions such as profile updates,
 * post deletions, admin actions, friend requests, etc.
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user", columnList = "auth_user_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_target_type", columnList = "target_type"),
                @Index(name = "idx_audit_target_id", columnList = "target_id"),
                @Index(name = "idx_audit_service", columnList = "service_name"),
                @Index(name = "idx_audit_created", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    // User context
    @Column(name = "auth_user_id")
    private Long authUserId;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    // Target entity
    @Column(name = "target_type", nullable = false, length = 100)
    private String targetType;

    @Column(name = "target_id", length = 200)
    private String targetId;

    /**
     * Previous state - stored as JSON string.
     * Truncated if too large. Sensitive data masked.
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * New state - stored as JSON string.
     * Truncated if too large. Sensitive data masked.
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

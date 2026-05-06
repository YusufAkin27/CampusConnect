package logging_service.entity;

import jakarta.persistence.*;
import logging_service.enums.SecurityEventType;
import logging_service.enums.SecuritySeverity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Security log entity.
 * Records security events such as failed logins, unauthorized access,
 * token errors, rate limiting, suspicious requests, etc.
 */
@Entity
@Table(
        name = "security_logs",
        indexes = {
                @Index(name = "idx_sec_event_type", columnList = "event_type"),
                @Index(name = "idx_sec_severity", columnList = "severity"),
                @Index(name = "idx_sec_user", columnList = "auth_user_id"),
                @Index(name = "idx_sec_ip", columnList = "client_ip"),
                @Index(name = "idx_sec_service", columnList = "service_name"),
                @Index(name = "idx_sec_created", columnList = "created_at"),
                @Index(name = "idx_sec_severity_created", columnList = "severity, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "auth_user_id")
    private Long authUserId;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private SecurityEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SecuritySeverity severity;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

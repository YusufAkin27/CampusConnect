package logging_service.entity;

import jakarta.persistence.*;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import lombok.*;

import java.time.LocalDateTime;

/**
 * General-purpose log entry entity.
 * Used for application, system, business, auth, and other general logs.
 *
 * TODO: Production scale için createdAt bazlı monthly partitioning önerilir.
 * Example: PARTITION BY RANGE (created_at) with monthly partitions.
 */
@Entity
@Table(
        name = "log_entries",
        indexes = {
                @Index(name = "idx_log_service", columnList = "service_name"),
                @Index(name = "idx_log_level", columnList = "level"),
                @Index(name = "idx_log_category", columnList = "category"),
                @Index(name = "idx_log_auth_user", columnList = "auth_user_id"),
                @Index(name = "idx_log_trace", columnList = "trace_id"),
                @Index(name = "idx_log_correlation", columnList = "correlation_id"),
                @Index(name = "idx_log_request", columnList = "request_id"),
                @Index(name = "idx_log_created", columnList = "created_at"),
                @Index(name = "idx_log_endpoint", columnList = "endpoint"),
                @Index(name = "idx_log_service_created", columnList = "service_name, created_at"),
                @Index(name = "idx_log_level_created", columnList = "level, created_at"),
                @Index(name = "idx_log_category_created", columnList = "category, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tracing fields
    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    // Service identification
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogLevel level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LogCategory category;

    // Log message
    @Column(nullable = false, length = 2000)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String details;

    // User context
    @Column(name = "auth_user_id")
    private Long authUserId;

    @Column(name = "username", length = 150)
    private String username;

    // Request context
    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "duration_ms")
    private Long durationMs;

    // Exception info
    @Column(name = "exception_class", length = 300)
    private String exceptionClass;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    /**
     * Stack trace - only for ERROR/FATAL.
     * Truncated if too large. Max 10000 chars.
     */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    // Environment info
    @Column(name = "environment", length = 20)
    private String environment;

    @Column(name = "host_name", length = 200)
    private String hostName;

    /**
     * Additional metadata stored as JSON string.
     * Sensitive fields must be masked before storing.
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

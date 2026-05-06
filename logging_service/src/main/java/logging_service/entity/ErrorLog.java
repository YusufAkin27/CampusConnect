package logging_service.entity;

import jakarta.persistence.*;
import logging_service.enums.ErrorSeverity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Error log entity.
 * Stores exceptions and system errors.
 * Stack trace is truncated to max 10000 chars.
 */
@Entity
@Table(
        name = "error_logs",
        indexes = {
                @Index(name = "idx_error_service", columnList = "service_name"),
                @Index(name = "idx_error_severity", columnList = "severity"),
                @Index(name = "idx_error_resolved", columnList = "resolved"),
                @Index(name = "idx_error_exception_class", columnList = "exception_class"),
                @Index(name = "idx_error_user", columnList = "auth_user_id"),
                @Index(name = "idx_error_created", columnList = "created_at"),
                @Index(name = "idx_error_severity_resolved", columnList = "severity, resolved")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "auth_user_id")
    private Long authUserId;

    @Column(name = "username", length = 150)
    private String username;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "exception_class", length = 300)
    private String exceptionClass;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    @Column(name = "root_cause", length = 2000)
    private String rootCause;

    /**
     * Stack trace - truncated to max 10000 chars.
     */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ErrorSeverity severity;

    @Column(name = "resolved", nullable = false)
    @Builder.Default
    private Boolean resolved = false;

    @Column(name = "resolved_by", length = 200)
    private String resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_note", length = 1000)
    private String resolutionNote;

    @Column(name = "environment", length = 20)
    private String environment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.resolved == null) {
            this.resolved = false;
        }
    }
}

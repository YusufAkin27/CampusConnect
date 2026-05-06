package logging_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * API request log entity.
 * Stores HTTP request/response details.
 * Body previews are truncated (max 2000 chars) and sensitive data is masked.
 */
@Entity
@Table(
        name = "api_request_logs",
        indexes = {
                @Index(name = "idx_api_service", columnList = "service_name"),
                @Index(name = "idx_api_endpoint", columnList = "endpoint"),
                @Index(name = "idx_api_status", columnList = "http_status"),
                @Index(name = "idx_api_user", columnList = "auth_user_id"),
                @Index(name = "idx_api_duration", columnList = "duration_ms"),
                @Index(name = "idx_api_created", columnList = "created_at"),
                @Index(name = "idx_api_service_created", columnList = "service_name, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestLog {

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

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "query_string", length = 1000)
    private String queryString;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Request body preview - max 2000 chars.
     * Sensitive fields (password, token, etc.) are masked with ***MASKED***.
     */
    @Column(name = "request_body_preview", length = 2000)
    private String requestBodyPreview;

    /**
     * Response body preview - max 2000 chars.
     * Sensitive fields are masked.
     */
    @Column(name = "response_body_preview", length = 2000)
    private String responseBodyPreview;

    @Column(name = "request_size_bytes")
    private Long requestSizeBytes;

    @Column(name = "response_size_bytes")
    private Long responseSizeBytes;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

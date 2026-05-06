package logging_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import logging_service.enums.SecurityEventType;
import logging_service.enums.SecuritySeverity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSecurityLogRequest {

    private String traceId;
    private String correlationId;
    private Long authUserId;
    private String username;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @NotNull(message = "Event type cannot be null")
    private SecurityEventType eventType;

    /**
     * If null, defaults to MEDIUM.
     * Can be auto-elevated based on event type:
     * - ACCESS_DENIED -> MEDIUM
     * - RATE_LIMIT_EXCEEDED -> HIGH
     * - SUSPICIOUS_REQUEST -> HIGH
     * - ADMIN_ACCESS -> MEDIUM
     */
    private SecuritySeverity severity;

    private String clientIp;
    private String userAgent;
    private String endpoint;
    private String httpMethod;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private String details;
}

package logging_service.dto.response;

import logging_service.enums.SecurityEventType;
import logging_service.enums.SecuritySeverity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityLogResponse {

    private Long id;
    private String traceId;
    private String correlationId;
    private Long authUserId;
    private String username;
    private String serviceName;
    private SecurityEventType eventType;
    private SecuritySeverity severity;
    private String clientIp;
    private String userAgent;
    private String endpoint;
    private String httpMethod;
    private String message;
    private String details;
    private LocalDateTime createdAt;
}

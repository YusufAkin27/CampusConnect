package logging_service.dto.response;

import logging_service.enums.AuditAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String traceId;
    private String correlationId;
    private Long authUserId;
    private String username;
    private String serviceName;
    private AuditAction action;
    private String targetType;
    private String targetId;
    private String oldValue;
    private String newValue;
    private String description;
    private String clientIp;
    private String userAgent;
    private LocalDateTime createdAt;
}

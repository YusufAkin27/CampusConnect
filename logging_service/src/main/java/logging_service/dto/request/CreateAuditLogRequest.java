package logging_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import logging_service.enums.AuditAction;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuditLogRequest {

    private String traceId;
    private String correlationId;
    private Long authUserId;
    private String username;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @NotNull(message = "Audit action cannot be null")
    private AuditAction action;

    @NotBlank(message = "Target type cannot be blank")
    private String targetType;

    private String targetId;
    private String oldValue;
    private String newValue;
    private String description;
    private String clientIp;
    private String userAgent;
}

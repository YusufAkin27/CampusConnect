package logging_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import logging_service.enums.ErrorSeverity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateErrorLogRequest {

    private String traceId;
    private String correlationId;
    private String requestId;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    private Long authUserId;
    private String username;
    private String endpoint;
    private String httpMethod;
    private Integer httpStatus;

    @NotBlank(message = "Exception class cannot be blank")
    private String exceptionClass;

    @NotBlank(message = "Exception message cannot be blank")
    private String exceptionMessage;

    private String rootCause;
    private String stackTrace;

    /**
     * If null, severity is auto-determined based on HTTP status:
     * - 500+ -> HIGH
     * - 400-499 -> MEDIUM
     * - Other -> LOW
     */
    private ErrorSeverity severity;

    private String environment;
}

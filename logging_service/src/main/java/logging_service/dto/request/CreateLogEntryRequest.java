package logging_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLogEntryRequest {

    private String traceId;
    private String correlationId;
    private String requestId;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @NotNull(message = "Log level cannot be null")
    private LogLevel level;

    @NotNull(message = "Log category cannot be null")
    private LogCategory category;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;

    private String details;
    private Long authUserId;
    private String username;
    private String clientIp;
    private String userAgent;
    private String endpoint;
    private String httpMethod;
    private Integer httpStatus;
    private Long durationMs;
    private String exceptionClass;
    private String exceptionMessage;
    private String stackTrace;
    private String environment;
    private String hostName;
    private Map<String, Object> metadata;
}

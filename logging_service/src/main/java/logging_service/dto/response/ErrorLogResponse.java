package logging_service.dto.response;

import logging_service.enums.ErrorSeverity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLogResponse {

    private Long id;
    private String traceId;
    private String correlationId;
    private String requestId;
    private String serviceName;
    private Long authUserId;
    private String username;
    private String endpoint;
    private String httpMethod;
    private Integer httpStatus;
    private String exceptionClass;
    private String exceptionMessage;
    private String rootCause;
    private String stackTrace;
    private ErrorSeverity severity;
    private Boolean resolved;
    private String resolvedBy;
    private LocalDateTime resolvedAt;
    private String resolutionNote;
    private String environment;
    private LocalDateTime createdAt;
}

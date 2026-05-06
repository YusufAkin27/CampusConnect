package logging_service.dto.response;

import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntryResponse {

    private Long id;
    private String traceId;
    private String correlationId;
    private String requestId;
    private String serviceName;
    private LogLevel level;
    private LogCategory category;
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
    private String environment;
    private String hostName;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}

package logging_service.dto.response;

import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lightweight log summary - used in list/search endpoints.
 * Does NOT include large fields like stackTrace, details, metadata.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSummaryResponse {

    private Long id;
    private String serviceName;
    private LogLevel level;
    private LogCategory category;
    private String message;
    private Long authUserId;
    private String endpoint;
    private Integer httpStatus;
    private Long durationMs;
    private LocalDateTime createdAt;
}

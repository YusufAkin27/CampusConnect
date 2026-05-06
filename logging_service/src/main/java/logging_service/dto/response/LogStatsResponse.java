package logging_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogStatsResponse {

    private Long totalLogs;
    private Long infoCount;
    private Long warnCount;
    private Long errorCount;
    private Long fatalCount;
    private Long apiRequestCount;
    private Long auditCount;
    private Long securityCount;
    private Long averageDurationMs;
    private Long criticalErrorCount;
    private LocalDateTime from;
    private LocalDateTime to;
}

package logging_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceLogStatsResponse {

    private String serviceName;
    private Long totalLogs;
    private Long errorCount;
    private Long warnCount;
    private Long apiRequestCount;
    private Long averageDurationMs;
    private LocalDateTime lastLogAt;
}

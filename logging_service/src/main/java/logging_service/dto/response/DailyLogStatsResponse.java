package logging_service.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogStatsResponse {

    private LocalDate date;
    private Long totalLogs;
    private Long errorCount;
    private Long warnCount;
    private Long securityCount;
    private Long apiRequestCount;
}

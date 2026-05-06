package logging_service.dto.request;

import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import logging_service.enums.SortType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSearchRequest {

    private String serviceName;
    private LogLevel level;
    private LogCategory category;
    private Long authUserId;
    private String traceId;
    private String correlationId;
    private String endpoint;
    private Integer httpStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private SortType sortType = SortType.NEWEST;
}

package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.dto.response.LogSummaryResponse;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import logging_service.enums.SortType;

import java.time.LocalDateTime;

public interface LogQueryService {

    DataResponseMessage<LogEntryResponse> getLogById(Long id);

    DataResponseMessage<PageResponse<LogSummaryResponse>> searchLogs(
            String serviceName,
            LogLevel level,
            LogCategory category,
            Long authUserId,
            String traceId,
            String correlationId,
            String endpoint,
            Integer httpStatus,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String keyword,
            int page,
            int size,
            SortType sortType
    );

    DataResponseMessage<PageResponse<LogSummaryResponse>> getLogsByService(
            String serviceName,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<LogSummaryResponse>> getLogsByTraceId(
            String traceId,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<LogSummaryResponse>> getErrorLevelLogs(
            int page,
            int size
    );
}

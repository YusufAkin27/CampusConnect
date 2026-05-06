package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.dto.response.LogSummaryResponse;
import logging_service.entity.LogEntry;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import logging_service.enums.SortType;
import logging_service.exception.LogNotFoundException;
import logging_service.mapper.LoggingMapper;
import logging_service.repository.LogEntryRepository;
import logging_service.service.LogQueryService;
import logging_service.util.PageResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LogQueryServiceImpl implements LogQueryService {

    private static final int MAX_PAGE_SIZE = 100;

    private final LogEntryRepository logEntryRepository;
    private final LoggingMapper loggingMapper;
    private final PageResponseConverter pageConverter;

    @Override
    public DataResponseMessage<LogEntryResponse> getLogById(Long id) {
        LogEntry logEntry = logEntryRepository.findById(id)
                .orElseThrow(() -> new LogNotFoundException(id));
        return DataResponseMessage.success("Log entry found", loggingMapper.toLogEntryResponse(logEntry));
    }

    @Override
    public DataResponseMessage<PageResponse<LogSummaryResponse>> searchLogs(
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
            SortType sortType) {

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, buildSort(sortType));

        Page<LogEntry> logPage = logEntryRepository.searchLogs(
                isBlank(serviceName) ? null : serviceName,
                level,
                category,
                authUserId,
                isBlank(traceId) ? null : traceId,
                isBlank(correlationId) ? null : correlationId,
                isBlank(endpoint) ? null : endpoint,
                httpStatus,
                startDate,
                endDate,
                isBlank(keyword) ? null : keyword,
                pageable
        );

        PageResponse<LogSummaryResponse> response = pageConverter.convert(logPage, loggingMapper::toLogSummaryResponse);
        return DataResponseMessage.success("Logs found", response);
    }

    @Override
    public DataResponseMessage<PageResponse<LogSummaryResponse>> getLogsByService(
            String serviceName, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LogEntry> logPage = logEntryRepository.findByServiceName(serviceName, pageable);
        PageResponse<LogSummaryResponse> response = pageConverter.convert(logPage, loggingMapper::toLogSummaryResponse);
        return DataResponseMessage.success("Logs for service: " + serviceName, response);
    }

    @Override
    public DataResponseMessage<PageResponse<LogSummaryResponse>> getLogsByTraceId(
            String traceId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<LogEntry> logPage = logEntryRepository.findByTraceId(traceId, pageable);
        PageResponse<LogSummaryResponse> response = pageConverter.convert(logPage, loggingMapper::toLogSummaryResponse);
        return DataResponseMessage.success("Logs for traceId: " + traceId, response);
    }

    @Override
    public DataResponseMessage<PageResponse<LogSummaryResponse>> getErrorLevelLogs(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LogEntry> logPage = logEntryRepository.findByLevel(LogLevel.ERROR, pageable);
        PageResponse<LogSummaryResponse> response = pageConverter.convert(logPage, loggingMapper::toLogSummaryResponse);
        return DataResponseMessage.success("Error level logs", response);
    }

    private Sort buildSort(SortType sortType) {
        if (sortType == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sortType) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case LEVEL -> Sort.by(Sort.Direction.ASC, "level");
            case SERVICE_NAME -> Sort.by(Sort.Direction.ASC, "serviceName");
            case DURATION_DESC -> Sort.by(Sort.Direction.DESC, "durationMs");
            case DURATION_ASC -> Sort.by(Sort.Direction.ASC, "durationMs");
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

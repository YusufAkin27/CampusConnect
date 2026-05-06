package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.dto.response.DailyLogStatsResponse;
import logging_service.dto.response.LogStatsResponse;
import logging_service.dto.response.ServiceLogStatsResponse;
import logging_service.enums.ErrorSeverity;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import logging_service.enums.SecuritySeverity;
import logging_service.repository.ApiRequestLogRepository;
import logging_service.repository.AuditLogRepository;
import logging_service.repository.ErrorLogRepository;
import logging_service.repository.LogEntryRepository;
import logging_service.repository.SecurityLogRepository;
import logging_service.service.LogStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LogStatsServiceImpl implements LogStatsService {

    private final LogEntryRepository logEntryRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final ErrorLogRepository errorLogRepository;

    @Override
    public DataResponseMessage<LogStatsResponse> getGeneralStats(
            LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate == null) startDate = LocalDateTime.now().minusDays(30);
        if (endDate == null) endDate = LocalDateTime.now();

        Long totalLogs = logEntryRepository.countByCreatedAtBetween(startDate, endDate);
        Long infoCount = logEntryRepository.countByLevel(LogLevel.INFO);
        Long warnCount = logEntryRepository.countByLevel(LogLevel.WARN);
        Long errorCount = logEntryRepository.countByLevel(LogLevel.ERROR);
        Long fatalCount = logEntryRepository.countByLevel(LogLevel.FATAL);
        Long apiRequestCount = logEntryRepository.countByCategory(LogCategory.API_REQUEST);
        Long auditCount = auditLogRepository.countByCreatedAtBetween(startDate, endDate);
        Long securityCount = securityLogRepository.countByCreatedAtBetween(startDate, endDate);
        Long criticalErrorCount = errorLogRepository.countBySeverity(ErrorSeverity.CRITICAL);

        Double avgDuration = logEntryRepository.getAverageDurationMs(startDate, endDate);
        Long averageDurationMs = avgDuration != null ? avgDuration.longValue() : 0L;

        LogStatsResponse stats = LogStatsResponse.builder()
                .totalLogs(totalLogs)
                .infoCount(infoCount)
                .warnCount(warnCount)
                .errorCount(errorCount)
                .fatalCount(fatalCount)
                .apiRequestCount(apiRequestCount)
                .auditCount(auditCount)
                .securityCount(securityCount)
                .averageDurationMs(averageDurationMs)
                .criticalErrorCount(criticalErrorCount)
                .from(startDate)
                .to(endDate)
                .build();

        return DataResponseMessage.success("General log statistics", stats);
    }

    @Override
    public DataResponseMessage<List<ServiceLogStatsResponse>> getStatsByService(
            LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate == null) startDate = LocalDateTime.now().minusDays(30);
        if (endDate == null) endDate = LocalDateTime.now();

        // Get all distinct service names from log entries
        List<String> serviceNames = Arrays.asList(
                "auth-service", "user-service", "post-service",
                "friend-service", "logging-service", "api-gateway"
        );

        List<ServiceLogStatsResponse> result = new ArrayList<>();
        for (String serviceName : serviceNames) {
            Long total = logEntryRepository.countByServiceNameAndCreatedAtBetween(serviceName, startDate, endDate);
            if (total == 0) continue;

            Long errorCount = errorLogRepository.countBySeverityAndCreatedAtBetween(ErrorSeverity.HIGH, startDate, endDate);
            Long warnCount = logEntryRepository.countByLevel(LogLevel.WARN);
            Long apiCount = apiRequestLogRepository.countByServiceNameAndCreatedAtBetween(serviceName, startDate, endDate);

            Double avgDuration = apiRequestLogRepository.getAverageDurationByService(serviceName);
            Long avgDurationMs = avgDuration != null ? avgDuration.longValue() : 0L;

            result.add(ServiceLogStatsResponse.builder()
                    .serviceName(serviceName)
                    .totalLogs(total)
                    .errorCount(errorCount)
                    .warnCount(warnCount)
                    .apiRequestCount(apiCount)
                    .averageDurationMs(avgDurationMs)
                    .lastLogAt(endDate)
                    .build());
        }

        return DataResponseMessage.success("Stats by service", result);
    }

    @Override
    public DataResponseMessage<List<DailyLogStatsResponse>> getDailyStats(
            LocalDate startDate, LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusDays(7);
        if (endDate == null) endDate = LocalDate.now();

        List<DailyLogStatsResponse> result = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.plusDays(1).atStartOfDay();

            Long totalLogs = logEntryRepository.countByCreatedAtBetween(dayStart, dayEnd);
            Long errorCount = errorLogRepository.countByCreatedAtBetween(dayStart, dayEnd);
            Long securityCount = securityLogRepository.countByCreatedAtBetween(dayStart, dayEnd);
            Long apiCount = apiRequestLogRepository.countByCreatedAtBetween(dayStart, dayEnd);
            Long warnCount = logEntryRepository.countByLevel(LogLevel.WARN);

            result.add(DailyLogStatsResponse.builder()
                    .date(current)
                    .totalLogs(totalLogs)
                    .errorCount(errorCount)
                    .warnCount(warnCount)
                    .securityCount(securityCount)
                    .apiRequestCount(apiCount)
                    .build());

            current = current.plusDays(1);
        }

        return DataResponseMessage.success("Daily log statistics", result);
    }
}

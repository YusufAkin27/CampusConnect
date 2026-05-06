package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.dto.request.BatchLogEntryRequest;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.dto.response.BatchLogResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.entity.LogEntry;
import logging_service.exception.BatchLogSizeExceededException;
import logging_service.mapper.LoggingMapper;
import logging_service.repository.LogEntryRepository;
import logging_service.service.LogIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LogIngestionService.
 * Handles both synchronous and asynchronous log creation.
 *
 * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
 * In production, a Kafka consumer should subscribe to 'campus-connect-logs' topic
 * and this service would be the consumer implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogIngestionServiceImpl implements LogIngestionService {

    private final LogEntryRepository logEntryRepository;
    private final LoggingMapper loggingMapper;

    @Value("${app.log.batch.max-size:500}")
    private int maxBatchSize;

    @Override
    @Transactional
    public DataResponseMessage<LogEntryResponse> createLog(CreateLogEntryRequest request) {
        LogEntry logEntry = loggingMapper.toLogEntry(request);
        LogEntry saved = logEntryRepository.save(logEntry);
        return DataResponseMessage.success("Log created successfully", loggingMapper.toLogEntryResponse(saved));
    }

    @Override
    @Transactional
    public DataResponseMessage<BatchLogResponse> createBatchLogs(BatchLogEntryRequest request) {
        List<CreateLogEntryRequest> logs = request.getLogs();

        if (logs.size() > maxBatchSize) {
            throw new BatchLogSizeExceededException(logs.size(), maxBatchSize);
        }

        int receivedCount = logs.size();
        int savedCount = 0;
        int failedCount = 0;
        List<String> errors = new ArrayList<>();

        List<LogEntry> validEntries = new ArrayList<>();

        // Validate each log individually - don't fail entire batch on single error
        for (int i = 0; i < logs.size(); i++) {
            try {
                CreateLogEntryRequest logRequest = logs.get(i);
                if (logRequest.getServiceName() == null || logRequest.getServiceName().isBlank()) {
                    errors.add("Log[" + i + "]: serviceName is required");
                    failedCount++;
                    continue;
                }
                if (logRequest.getLevel() == null) {
                    errors.add("Log[" + i + "]: level is required");
                    failedCount++;
                    continue;
                }
                if (logRequest.getCategory() == null) {
                    errors.add("Log[" + i + "]: category is required");
                    failedCount++;
                    continue;
                }
                if (logRequest.getMessage() == null || logRequest.getMessage().isBlank()) {
                    errors.add("Log[" + i + "]: message is required");
                    failedCount++;
                    continue;
                }
                validEntries.add(loggingMapper.toLogEntry(logRequest));
            } catch (Exception e) {
                errors.add("Log[" + i + "]: " + e.getMessage());
                failedCount++;
            }
        }

        // Batch insert all valid entries
        if (!validEntries.isEmpty()) {
            try {
                logEntryRepository.saveAll(validEntries);
                savedCount = validEntries.size();
            } catch (Exception e) {
                log.error("Failed to batch insert {} log entries: {}", validEntries.size(), e.getMessage());
                failedCount += validEntries.size();
                savedCount = 0;
                errors.add("Batch insert failed: " + e.getMessage());
            }
        }

        BatchLogResponse batchResponse = BatchLogResponse.builder()
                .receivedCount(receivedCount)
                .savedCount(savedCount)
                .failedCount(failedCount)
                .errors(errors.isEmpty() ? null : errors)
                .build();

        return DataResponseMessage.success(
                "Batch log processing completed. Saved: " + savedCount + ", Failed: " + failedCount,
                batchResponse
        );
    }

    /**
     * Async log creation - does not block the calling service.
     * If log write fails, it is caught and logged locally without propagating.
     *
     * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
     */
    @Async("loggingAsyncExecutor")
    @Override
    @Transactional
    public void createLogAsync(CreateLogEntryRequest request) {
        try {
            LogEntry logEntry = loggingMapper.toLogEntry(request);
            logEntryRepository.save(logEntry);
        } catch (Exception e) {
            // Log failure should NOT propagate to calling service
            log.error("Async log creation failed for service '{}': {}",
                    request.getServiceName(), e.getMessage());
        }
    }

    /**
     * Async batch log creation.
     *
     * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
     */
    @Async("loggingAsyncExecutor")
    @Override
    @Transactional
    public void createBatchLogsAsync(List<CreateLogEntryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<LogEntry> validEntries = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            try {
                validEntries.add(loggingMapper.toLogEntry(requests.get(i)));
            } catch (Exception e) {
                log.warn("Async batch: failed to map log entry [{}]: {}", i, e.getMessage());
            }
        }

        if (!validEntries.isEmpty()) {
            try {
                logEntryRepository.saveAll(validEntries);
            } catch (Exception e) {
                log.error("Async batch insert failed for {} entries: {}", validEntries.size(), e.getMessage());
            }
        }
    }
}

package logging_service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.dto.response.LogEntryResponse;
import logging_service.dto.response.LogSummaryResponse;
import logging_service.entity.LogEntry;
import logging_service.util.LogTruncationUtil;
import logging_service.util.SensitiveDataMasker;
import logging_service.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingMapper {

    private final TraceIdUtil traceIdUtil;
    private final LogTruncationUtil truncationUtil;
    private final SensitiveDataMasker masker;
    private final ObjectMapper objectMapper;

    public LogEntry toLogEntry(CreateLogEntryRequest request) {
        String metadataJson = null;
        if (request.getMetadata() != null) {
            try {
                Map<String, Object> maskedMetadata = masker.maskMap(request.getMetadata());
                metadataJson = objectMapper.writeValueAsString(maskedMetadata);
                metadataJson = truncationUtil.truncateMetadata(metadataJson);
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize metadata for log entry: {}", e.getMessage());
            }
        }

        return LogEntry.builder()
                .traceId(traceIdUtil.getOrGenerateTraceId(request.getTraceId()))
                .correlationId(traceIdUtil.getOrGenerateCorrelationId(request.getCorrelationId()))
                .requestId(request.getRequestId() != null ? request.getRequestId() : traceIdUtil.generateRequestId())
                .serviceName(request.getServiceName())
                .level(request.getLevel())
                .category(request.getCategory())
                .message(truncationUtil.truncateMessage(request.getMessage()))
                .details(truncationUtil.truncateDetails(request.getDetails()))
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .clientIp(request.getClientIp())
                .userAgent(request.getUserAgent())
                .endpoint(request.getEndpoint())
                .httpMethod(request.getHttpMethod())
                .httpStatus(request.getHttpStatus())
                .durationMs(request.getDurationMs())
                .exceptionClass(request.getExceptionClass())
                .exceptionMessage(truncationUtil.truncateExceptionMessage(request.getExceptionMessage()))
                .stackTrace(truncationUtil.truncateStackTrace(request.getStackTrace()))
                .environment(request.getEnvironment())
                .hostName(request.getHostName())
                .metadata(metadataJson)
                .build();
    }

    public LogEntryResponse toLogEntryResponse(LogEntry logEntry) {
        Map<String, Object> metadata = null;
        if (logEntry.getMetadata() != null) {
            try {
                metadata = objectMapper.readValue(logEntry.getMetadata(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize metadata for log id {}: {}", logEntry.getId(), e.getMessage());
            }
        }

        return LogEntryResponse.builder()
                .id(logEntry.getId())
                .traceId(logEntry.getTraceId())
                .correlationId(logEntry.getCorrelationId())
                .requestId(logEntry.getRequestId())
                .serviceName(logEntry.getServiceName())
                .level(logEntry.getLevel())
                .category(logEntry.getCategory())
                .message(logEntry.getMessage())
                .details(logEntry.getDetails())
                .authUserId(logEntry.getAuthUserId())
                .username(logEntry.getUsername())
                .clientIp(logEntry.getClientIp())
                .userAgent(logEntry.getUserAgent())
                .endpoint(logEntry.getEndpoint())
                .httpMethod(logEntry.getHttpMethod())
                .httpStatus(logEntry.getHttpStatus())
                .durationMs(logEntry.getDurationMs())
                .exceptionClass(logEntry.getExceptionClass())
                .exceptionMessage(logEntry.getExceptionMessage())
                .environment(logEntry.getEnvironment())
                .hostName(logEntry.getHostName())
                .metadata(metadata)
                .createdAt(logEntry.getCreatedAt())
                .build();
    }

    public LogSummaryResponse toLogSummaryResponse(LogEntry logEntry) {
        return LogSummaryResponse.builder()
                .id(logEntry.getId())
                .serviceName(logEntry.getServiceName())
                .level(logEntry.getLevel())
                .category(logEntry.getCategory())
                .message(logEntry.getMessage())
                .authUserId(logEntry.getAuthUserId())
                .endpoint(logEntry.getEndpoint())
                .httpStatus(logEntry.getHttpStatus())
                .durationMs(logEntry.getDurationMs())
                .createdAt(logEntry.getCreatedAt())
                .build();
    }
}

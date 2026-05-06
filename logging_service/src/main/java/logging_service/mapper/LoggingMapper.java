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

    public LogEntryResponse toLogEntryResponse(LogEntry log) {
        Map<String, Object> metadata = null;
        if (log.getMetadata() != null) {
            try {
                metadata = objectMapper.readValue(log.getMetadata(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize metadata for log id {}: {}", log.getId(), e.getMessage());
            }
        }

        return LogEntryResponse.builder()
                .id(log.getId())
                .traceId(log.getTraceId())
                .correlationId(log.getCorrelationId())
                .requestId(log.getRequestId())
                .serviceName(log.getServiceName())
                .level(log.getLevel())
                .category(log.getCategory())
                .message(log.getMessage())
                .details(log.getDetails())
                .authUserId(log.getAuthUserId())
                .username(log.getUsername())
                .clientIp(log.getClientIp())
                .userAgent(log.getUserAgent())
                .endpoint(log.getEndpoint())
                .httpMethod(log.getHttpMethod())
                .httpStatus(log.getHttpStatus())
                .durationMs(log.getDurationMs())
                .exceptionClass(log.getExceptionClass())
                .exceptionMessage(log.getExceptionMessage())
                .environment(log.getEnvironment())
                .hostName(log.getHostName())
                .metadata(metadata)
                .createdAt(log.getCreatedAt())
                .build();
    }

    public LogSummaryResponse toLogSummaryResponse(LogEntry log) {
        return LogSummaryResponse.builder()
                .id(log.getId())
                .serviceName(log.getServiceName())
                .level(log.getLevel())
                .category(log.getCategory())
                .message(log.getMessage())
                .authUserId(log.getAuthUserId())
                .endpoint(log.getEndpoint())
                .httpStatus(log.getHttpStatus())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

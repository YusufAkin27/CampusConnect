package logging_service.mapper;

import logging_service.dto.request.CreateErrorLogRequest;
import logging_service.dto.response.ErrorLogResponse;
import logging_service.entity.ErrorLog;
import logging_service.enums.ErrorSeverity;
import logging_service.util.LogTruncationUtil;
import logging_service.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorLogMapper {

    private final TraceIdUtil traceIdUtil;
    private final LogTruncationUtil truncationUtil;

    public ErrorLog toErrorLog(CreateErrorLogRequest request) {
        // Auto-determine severity based on HTTP status if not provided
        ErrorSeverity severity = request.getSeverity();
        if (severity == null) {
            if (request.getHttpStatus() != null && request.getHttpStatus() >= 500) {
                severity = ErrorSeverity.HIGH;
            } else if (request.getHttpStatus() != null && request.getHttpStatus() >= 400) {
                severity = ErrorSeverity.MEDIUM;
            } else {
                severity = ErrorSeverity.LOW;
            }
        }

        return ErrorLog.builder()
                .traceId(traceIdUtil.getOrGenerateTraceId(request.getTraceId()))
                .correlationId(traceIdUtil.getOrGenerateCorrelationId(request.getCorrelationId()))
                .requestId(request.getRequestId() != null ? request.getRequestId() : traceIdUtil.generateRequestId())
                .serviceName(request.getServiceName())
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .endpoint(request.getEndpoint())
                .httpMethod(request.getHttpMethod())
                .httpStatus(request.getHttpStatus())
                .exceptionClass(request.getExceptionClass())
                .exceptionMessage(truncationUtil.truncateExceptionMessage(request.getExceptionMessage()))
                .rootCause(truncationUtil.truncate(request.getRootCause(), 2000))
                .stackTrace(truncationUtil.truncateStackTrace(request.getStackTrace()))
                .severity(severity)
                .resolved(false)
                .environment(request.getEnvironment())
                .build();
    }

    public ErrorLogResponse toErrorLogResponse(ErrorLog log) {
        return ErrorLogResponse.builder()
                .id(log.getId())
                .traceId(log.getTraceId())
                .correlationId(log.getCorrelationId())
                .requestId(log.getRequestId())
                .serviceName(log.getServiceName())
                .authUserId(log.getAuthUserId())
                .username(log.getUsername())
                .endpoint(log.getEndpoint())
                .httpMethod(log.getHttpMethod())
                .httpStatus(log.getHttpStatus())
                .exceptionClass(log.getExceptionClass())
                .exceptionMessage(log.getExceptionMessage())
                .rootCause(log.getRootCause())
                .stackTrace(log.getStackTrace())
                .severity(log.getSeverity())
                .resolved(log.getResolved())
                .resolvedBy(log.getResolvedBy())
                .resolvedAt(log.getResolvedAt())
                .resolutionNote(log.getResolutionNote())
                .environment(log.getEnvironment())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

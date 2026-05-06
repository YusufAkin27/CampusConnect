package logging_service.mapper;

import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.dto.response.ApiRequestLogResponse;
import logging_service.entity.ApiRequestLog;
import logging_service.util.LogTruncationUtil;
import logging_service.util.SensitiveDataMasker;
import logging_service.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiRequestLogMapper {

    private final TraceIdUtil traceIdUtil;
    private final LogTruncationUtil truncationUtil;
    private final SensitiveDataMasker masker;

    public ApiRequestLog toApiRequestLog(CreateApiRequestLogRequest request) {
        // Determine success if not provided: 2xx and 3xx are success
        Boolean success = request.getSuccess();
        if (success == null && request.getHttpStatus() != null) {
            success = request.getHttpStatus() >= 200 && request.getHttpStatus() < 400;
        }

        return ApiRequestLog.builder()
                .traceId(traceIdUtil.getOrGenerateTraceId(request.getTraceId()))
                .correlationId(traceIdUtil.getOrGenerateCorrelationId(request.getCorrelationId()))
                .requestId(request.getRequestId() != null ? request.getRequestId() : traceIdUtil.generateRequestId())
                .serviceName(request.getServiceName())
                .authUserId(request.getAuthUserId())
                .httpMethod(request.getHttpMethod())
                .endpoint(request.getEndpoint())
                .queryString(request.getQueryString())
                .httpStatus(request.getHttpStatus())
                .durationMs(request.getDurationMs() != null ? request.getDurationMs() : 0L)
                .clientIp(request.getClientIp())
                .userAgent(request.getUserAgent())
                .requestBodyPreview(masker.mask(truncationUtil.truncateBodyPreview(request.getRequestBodyPreview())))
                .responseBodyPreview(masker.mask(truncationUtil.truncateBodyPreview(request.getResponseBodyPreview())))
                .requestSizeBytes(request.getRequestSizeBytes())
                .responseSizeBytes(request.getResponseSizeBytes())
                .success(success)
                .build();
    }

    public ApiRequestLogResponse toApiRequestLogResponse(ApiRequestLog log) {
        return ApiRequestLogResponse.builder()
                .id(log.getId())
                .traceId(log.getTraceId())
                .correlationId(log.getCorrelationId())
                .requestId(log.getRequestId())
                .serviceName(log.getServiceName())
                .authUserId(log.getAuthUserId())
                .httpMethod(log.getHttpMethod())
                .endpoint(log.getEndpoint())
                .queryString(log.getQueryString())
                .httpStatus(log.getHttpStatus())
                .durationMs(log.getDurationMs())
                .clientIp(log.getClientIp())
                .userAgent(log.getUserAgent())
                .requestBodyPreview(log.getRequestBodyPreview())
                .responseBodyPreview(log.getResponseBodyPreview())
                .requestSizeBytes(log.getRequestSizeBytes())
                .responseSizeBytes(log.getResponseSizeBytes())
                .success(log.getSuccess())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

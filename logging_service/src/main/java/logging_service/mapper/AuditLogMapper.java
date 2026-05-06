package logging_service.mapper;

import logging_service.dto.request.CreateAuditLogRequest;
import logging_service.dto.response.AuditLogResponse;
import logging_service.entity.AuditLog;
import logging_service.util.LogTruncationUtil;
import logging_service.util.SensitiveDataMasker;
import logging_service.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogMapper {

    private final TraceIdUtil traceIdUtil;
    private final LogTruncationUtil truncationUtil;
    private final SensitiveDataMasker masker;

    public AuditLog toAuditLog(CreateAuditLogRequest request) {
        return AuditLog.builder()
                .traceId(traceIdUtil.getOrGenerateTraceId(request.getTraceId()))
                .correlationId(traceIdUtil.getOrGenerateCorrelationId(request.getCorrelationId()))
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .serviceName(request.getServiceName())
                .action(request.getAction())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .oldValue(masker.mask(truncationUtil.truncateOldNewValue(request.getOldValue())))
                .newValue(masker.mask(truncationUtil.truncateOldNewValue(request.getNewValue())))
                .description(request.getDescription())
                .clientIp(request.getClientIp())
                .userAgent(request.getUserAgent())
                .build();
    }

    public AuditLogResponse toAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .traceId(log.getTraceId())
                .correlationId(log.getCorrelationId())
                .authUserId(log.getAuthUserId())
                .username(log.getUsername())
                .serviceName(log.getServiceName())
                .action(log.getAction())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .description(log.getDescription())
                .clientIp(log.getClientIp())
                .userAgent(log.getUserAgent())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

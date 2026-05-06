package logging_service.mapper;

import logging_service.dto.request.CreateSecurityLogRequest;
import logging_service.dto.response.SecurityLogResponse;
import logging_service.entity.SecurityLog;
import logging_service.enums.SecurityEventType;
import logging_service.enums.SecuritySeverity;
import logging_service.util.TraceIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityLogMapper {

    private final TraceIdUtil traceIdUtil;

    public SecurityLog toSecurityLog(CreateSecurityLogRequest request) {
        // Auto-determine severity if not provided
        SecuritySeverity severity = request.getSeverity();
        if (severity == null) {
            severity = determineSeverity(request.getEventType());
        }

        return SecurityLog.builder()
                .traceId(traceIdUtil.getOrGenerateTraceId(request.getTraceId()))
                .correlationId(traceIdUtil.getOrGenerateCorrelationId(request.getCorrelationId()))
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .serviceName(request.getServiceName())
                .eventType(request.getEventType())
                .severity(severity)
                .clientIp(request.getClientIp())
                .userAgent(request.getUserAgent())
                .endpoint(request.getEndpoint())
                .httpMethod(request.getHttpMethod())
                .message(request.getMessage())
                .details(request.getDetails())
                .build();
    }

    public SecurityLogResponse toSecurityLogResponse(SecurityLog log) {
        return SecurityLogResponse.builder()
                .id(log.getId())
                .traceId(log.getTraceId())
                .correlationId(log.getCorrelationId())
                .authUserId(log.getAuthUserId())
                .username(log.getUsername())
                .serviceName(log.getServiceName())
                .eventType(log.getEventType())
                .severity(log.getSeverity())
                .clientIp(log.getClientIp())
                .userAgent(log.getUserAgent())
                .endpoint(log.getEndpoint())
                .httpMethod(log.getHttpMethod())
                .message(log.getMessage())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }

    /**
     * Determines severity based on event type.
     */
    private SecuritySeverity determineSeverity(SecurityEventType eventType) {
        if (eventType == null) return SecuritySeverity.MEDIUM;
        return switch (eventType) {
            case RATE_LIMIT_EXCEEDED, SUSPICIOUS_REQUEST -> SecuritySeverity.HIGH;
            case ACCESS_DENIED, ADMIN_ACCESS, LOGIN_FAILURE, TOKEN_EXPIRED, TOKEN_INVALID -> SecuritySeverity.MEDIUM;
            case LOGIN_SUCCESS, LOGOUT, PASSWORD_RESET_REQUEST, PASSWORD_CHANGED -> SecuritySeverity.LOW;
            default -> SecuritySeverity.MEDIUM;
        };
    }
}

package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateAuditLogRequest;
import logging_service.dto.response.AuditLogResponse;

public interface AuditLogService {

    DataResponseMessage<AuditLogResponse> createAuditLog(CreateAuditLogRequest request);

    DataResponseMessage<PageResponse<AuditLogResponse>> getAuditLogsByUser(
            Long authUserId,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<AuditLogResponse>> getAuditLogsByTarget(
            String targetType,
            String targetId,
            int page,
            int size
    );
}

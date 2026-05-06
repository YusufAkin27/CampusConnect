package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateSecurityLogRequest;
import logging_service.dto.response.SecurityLogResponse;

public interface SecurityLogService {

    DataResponseMessage<SecurityLogResponse> createSecurityLog(CreateSecurityLogRequest request);

    DataResponseMessage<PageResponse<SecurityLogResponse>> getSecurityLogsByUser(
            Long authUserId,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<SecurityLogResponse>> getSecurityLogsByIp(
            String clientIp,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<SecurityLogResponse>> getCriticalSecurityLogs(
            int page,
            int size
    );
}

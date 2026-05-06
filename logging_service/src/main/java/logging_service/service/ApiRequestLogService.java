package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.dto.response.ApiRequestLogResponse;

public interface ApiRequestLogService {

    DataResponseMessage<ApiRequestLogResponse> createApiRequestLog(CreateApiRequestLogRequest request);

    DataResponseMessage<PageResponse<ApiRequestLogResponse>> getSlowRequests(
            Long minDurationMs,
            int page,
            int size
    );

    DataResponseMessage<PageResponse<ApiRequestLogResponse>> getApiRequestsByService(
            String serviceName,
            int page,
            int size
    );
}

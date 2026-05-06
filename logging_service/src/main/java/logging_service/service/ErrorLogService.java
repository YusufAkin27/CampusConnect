package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateErrorLogRequest;
import logging_service.dto.request.ResolveErrorLogRequest;
import logging_service.dto.response.ErrorLogResponse;

public interface ErrorLogService {

    DataResponseMessage<ErrorLogResponse> createErrorLog(CreateErrorLogRequest request);

    DataResponseMessage<ErrorLogResponse> getErrorLogById(Long id);

    DataResponseMessage<PageResponse<ErrorLogResponse>> getUnresolvedErrors(int page, int size);

    DataResponseMessage<PageResponse<ErrorLogResponse>> getCriticalErrors(int page, int size);

    DataResponseMessage<ErrorLogResponse> resolveErrorLog(Long errorLogId, ResolveErrorLogRequest request);
}

package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.dto.response.ApiRequestLogResponse;
import logging_service.entity.ApiRequestLog;
import logging_service.mapper.ApiRequestLogMapper;
import logging_service.repository.ApiRequestLogRepository;
import logging_service.service.ApiRequestLogService;
import logging_service.util.PageResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiRequestLogServiceImpl implements ApiRequestLogService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ApiRequestLogRepository apiRequestLogRepository;
    private final ApiRequestLogMapper apiRequestLogMapper;
    private final PageResponseConverter pageConverter;

    @Override
    @Transactional
    public DataResponseMessage<ApiRequestLogResponse> createApiRequestLog(CreateApiRequestLogRequest request) {
        ApiRequestLog apiRequestLog = apiRequestLogMapper.toApiRequestLog(request);
        ApiRequestLog saved = apiRequestLogRepository.save(apiRequestLog);
        return DataResponseMessage.success("API request log created", apiRequestLogMapper.toApiRequestLogResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ApiRequestLogResponse>> getSlowRequests(
            Long minDurationMs, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size);
        Page<ApiRequestLog> logPage = apiRequestLogRepository.findSlowRequests(null, minDurationMs, pageable);
        PageResponse<ApiRequestLogResponse> response = pageConverter.convert(logPage, apiRequestLogMapper::toApiRequestLogResponse);
        return DataResponseMessage.success("Slow requests (>=" + minDurationMs + "ms)", response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ApiRequestLogResponse>> getApiRequestsByService(
            String serviceName, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ApiRequestLog> logPage = apiRequestLogRepository.findByServiceName(serviceName, pageable);
        PageResponse<ApiRequestLogResponse> response = pageConverter.convert(logPage, apiRequestLogMapper::toApiRequestLogResponse);
        return DataResponseMessage.success("API request logs for service: " + serviceName, response);
    }
}

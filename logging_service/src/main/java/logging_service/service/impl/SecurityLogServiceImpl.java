package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateSecurityLogRequest;
import logging_service.dto.response.SecurityLogResponse;
import logging_service.entity.SecurityLog;
import logging_service.enums.SecuritySeverity;
import logging_service.mapper.SecurityLogMapper;
import logging_service.repository.SecurityLogRepository;
import logging_service.service.SecurityLogService;
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
public class SecurityLogServiceImpl implements SecurityLogService {

    private static final int MAX_PAGE_SIZE = 100;

    private final SecurityLogRepository securityLogRepository;
    private final SecurityLogMapper securityLogMapper;
    private final PageResponseConverter pageConverter;

    @Override
    @Transactional
    public DataResponseMessage<SecurityLogResponse> createSecurityLog(CreateSecurityLogRequest request) {
        SecurityLog securityLog = securityLogMapper.toSecurityLog(request);
        SecurityLog saved = securityLogRepository.save(securityLog);
        return DataResponseMessage.success("Security log created", securityLogMapper.toSecurityLogResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<SecurityLogResponse>> getSecurityLogsByUser(
            Long authUserId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SecurityLog> logPage = securityLogRepository.findByAuthUserId(authUserId, pageable);
        PageResponse<SecurityLogResponse> response = pageConverter.convert(logPage, securityLogMapper::toSecurityLogResponse);
        return DataResponseMessage.success("Security logs for user: " + authUserId, response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<SecurityLogResponse>> getSecurityLogsByIp(
            String clientIp, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SecurityLog> logPage = securityLogRepository.findByClientIp(clientIp, pageable);
        PageResponse<SecurityLogResponse> response = pageConverter.convert(logPage, securityLogMapper::toSecurityLogResponse);
        return DataResponseMessage.success("Security logs for IP: " + clientIp, response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<SecurityLogResponse>> getCriticalSecurityLogs(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SecurityLog> logPage = securityLogRepository.findBySeverity(SecuritySeverity.CRITICAL, pageable);
        PageResponse<SecurityLogResponse> response = pageConverter.convert(logPage, securityLogMapper::toSecurityLogResponse);
        return DataResponseMessage.success("Critical security logs", response);
    }
}

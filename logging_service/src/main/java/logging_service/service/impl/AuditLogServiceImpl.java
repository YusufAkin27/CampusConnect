package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateAuditLogRequest;
import logging_service.dto.response.AuditLogResponse;
import logging_service.entity.AuditLog;
import logging_service.mapper.AuditLogMapper;
import logging_service.repository.AuditLogRepository;
import logging_service.service.AuditLogService;
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
public class AuditLogServiceImpl implements AuditLogService {

    private static final int MAX_PAGE_SIZE = 100;

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final PageResponseConverter pageConverter;

    @Override
    @Transactional
    public DataResponseMessage<AuditLogResponse> createAuditLog(CreateAuditLogRequest request) {
        AuditLog auditLog = auditLogMapper.toAuditLog(request);
        AuditLog saved = auditLogRepository.save(auditLog);
        return DataResponseMessage.success("Audit log created successfully", auditLogMapper.toAuditLogResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<AuditLogResponse>> getAuditLogsByUser(
            Long authUserId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> auditPage = auditLogRepository.findByAuthUserId(authUserId, pageable);
        PageResponse<AuditLogResponse> response = pageConverter.convert(auditPage, auditLogMapper::toAuditLogResponse);
        return DataResponseMessage.success("Audit logs for user: " + authUserId, response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<AuditLogResponse>> getAuditLogsByTarget(
            String targetType, String targetId, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> auditPage = auditLogRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable);
        PageResponse<AuditLogResponse> response = pageConverter.convert(auditPage, auditLogMapper::toAuditLogResponse);
        return DataResponseMessage.success("Audit logs for target " + targetType + ":" + targetId, response);
    }
}

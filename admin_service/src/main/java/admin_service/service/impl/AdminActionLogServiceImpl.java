package admin_service.service.impl;

import admin_service.dto.request.ActionLogFilterRequest;
import admin_service.dto.response.AdminActionLogResponse;
import admin_service.entity.AdminActionLog;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import admin_service.mapper.AdminActionLogMapper;
import admin_service.repository.AdminActionLogRepository;
import admin_service.service.AdminActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminActionLogServiceImpl implements AdminActionLogService {

    private final AdminActionLogRepository actionLogRepository;
    private final AdminActionLogMapper actionLogMapper;

    @Override
    public AdminActionLog logAction(Long adminId, ActionType actionType, TargetType targetType,
                                     Long targetId, String description) {
        AdminActionLog log2 = AdminActionLog.builder()
                .adminId(adminId).actionType(actionType)
                .targetType(targetType).targetId(targetId)
                .description(description).build();
        return actionLogRepository.save(log2);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminActionLogResponse getLogById(Long logId) {
        return actionLogMapper.toResponse(actionLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("Action log not found: " + logId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminActionLogResponse> getAllLogs(Pageable pageable) {
        return actionLogRepository.findAll(pageable).map(actionLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminActionLogResponse> getLogsByAdminId(Long adminId, Pageable pageable) {
        return actionLogRepository.findByAdminId(adminId, pageable).map(actionLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminActionLogResponse> getLogsByTarget(TargetType targetType, Long targetId, Pageable pageable) {
        return actionLogRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable)
                .map(actionLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminActionLogResponse> filterLogs(ActionLogFilterRequest filter, Pageable pageable) {
        return actionLogRepository.findByFilters(
                filter.getAdminId(), filter.getActionType(), filter.getTargetType(),
                filter.getStartDate(), filter.getEndDate(), pageable
        ).map(actionLogMapper::toResponse);
    }
}

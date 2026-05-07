package admin_service.service;

import admin_service.dto.request.ActionLogFilterRequest;
import admin_service.dto.response.AdminActionLogResponse;
import admin_service.entity.AdminActionLog;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminActionLogService {

    AdminActionLog logAction(Long adminId, ActionType actionType, TargetType targetType,
                              Long targetId, String description);

    AdminActionLogResponse getLogById(Long logId);

    Page<AdminActionLogResponse> getAllLogs(Pageable pageable);

    Page<AdminActionLogResponse> getLogsByAdminId(Long adminId, Pageable pageable);

    Page<AdminActionLogResponse> getLogsByTarget(TargetType targetType, Long targetId, Pageable pageable);

    Page<AdminActionLogResponse> filterLogs(ActionLogFilterRequest filter, Pageable pageable);
}

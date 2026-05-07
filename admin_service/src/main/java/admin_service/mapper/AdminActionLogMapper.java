package admin_service.mapper;

import admin_service.dto.response.AdminActionLogResponse;
import admin_service.entity.AdminActionLog;
import org.springframework.stereotype.Component;

@Component
public class AdminActionLogMapper {

    public AdminActionLogResponse toResponse(AdminActionLog entity) {
        if (entity == null) return null;
        return AdminActionLogResponse.builder()
                .id(entity.getId())
                .adminId(entity.getAdminId())
                .actionType(entity.getActionType())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .description(entity.getDescription())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

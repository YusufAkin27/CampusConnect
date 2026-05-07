package admin_service.mapper;

import admin_service.dto.response.UserBanRecordResponse;
import admin_service.entity.UserBanRecord;
import org.springframework.stereotype.Component;

@Component
public class UserBanRecordMapper {

    public UserBanRecordResponse toResponse(UserBanRecord entity) {
        if (entity == null) return null;
        return UserBanRecordResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bannedByAdminId(entity.getBannedByAdminId())
                .reason(entity.getReason())
                .banType(entity.getBanType())
                .startedAt(entity.getStartedAt())
                .expiresAt(entity.getExpiresAt())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

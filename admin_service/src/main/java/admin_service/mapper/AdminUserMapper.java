package admin_service.mapper;

import admin_service.dto.response.AdminUserDetailResponse;
import admin_service.dto.response.AdminUserResponse;
import admin_service.entity.AdminUser;
import admin_service.security.RolePermissionMapping;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {

    public AdminUserResponse toResponse(AdminUser entity) {
        if (entity == null) return null;
        return AdminUserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .build();
    }

    public AdminUserDetailResponse toDetailResponse(AdminUser entity) {
        if (entity == null) return null;
        return AdminUserDetailResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .status(entity.getStatus())
                .permissions(RolePermissionMapping.getPermissions(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .build();
    }
}

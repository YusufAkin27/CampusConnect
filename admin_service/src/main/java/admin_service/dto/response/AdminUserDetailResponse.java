package admin_service.dto.response;

import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import admin_service.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private AdminRole role;
    private AdminStatus status;
    private Set<Permission> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}

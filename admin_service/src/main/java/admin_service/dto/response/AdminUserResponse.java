package admin_service.dto.response;

import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private AdminRole role;
    private AdminStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}

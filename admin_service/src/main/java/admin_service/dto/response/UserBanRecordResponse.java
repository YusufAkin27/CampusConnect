package admin_service.dto.response;

import admin_service.enums.BanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBanRecordResponse {
    private Long id;
    private Long userId;
    private Long bannedByAdminId;
    private String reason;
    private BanType banType;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean active;
    private LocalDateTime createdAt;
}

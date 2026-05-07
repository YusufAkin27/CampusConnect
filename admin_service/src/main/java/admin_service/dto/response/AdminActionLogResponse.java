package admin_service.dto.response;

import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminActionLogResponse {
    private Long id;
    private Long adminId;
    private ActionType actionType;
    private TargetType targetType;
    private Long targetId;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}

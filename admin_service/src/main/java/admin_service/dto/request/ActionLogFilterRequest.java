package admin_service.dto.request;

import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Filter criteria for querying admin action logs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionLogFilterRequest {

    private Long adminId;
    private ActionType actionType;
    private TargetType targetType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

package admin_service.dto.request;

import admin_service.enums.ReportReason;
import admin_service.enums.TargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    @NotNull(message = "Reporter user ID is required")
    private Long reporterUserId;

    @NotNull(message = "Target type is required")
    private TargetType targetType;

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "Report reason is required")
    private ReportReason reason;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;
}

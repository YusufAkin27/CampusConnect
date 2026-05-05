package post_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import post_service.enums.ReportStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportStatusRequest {

    @NotNull(message = "Report status must not be null")
    private ReportStatus status;
}

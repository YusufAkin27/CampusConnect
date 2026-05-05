package post_service.dto.response;

import lombok.*;
import post_service.enums.ReportReason;
import post_service.enums.ReportStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long targetId;
    private String targetType;
    private Long reporterAuthUserId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}

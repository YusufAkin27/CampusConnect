package post_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import post_service.enums.ReportReason;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCommentRequest {

    @NotNull(message = "Report reason must not be null")
    private ReportReason reason;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}

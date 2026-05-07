package admin_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveReportRequest {

    @NotBlank(message = "Resolution note is required")
    @Size(max = 1000, message = "Resolution note must be at most 1000 characters")
    private String resolutionNote;

    /** Optional: action to take. e.g., BAN_USER, HIDE_POST, DELETE_POST, DELETE_MEDIA, WARN_USER, NONE */
    private String actionToTake;

    /** If banning user, the ban duration details */
    private BanUserRequest banDetails;
}

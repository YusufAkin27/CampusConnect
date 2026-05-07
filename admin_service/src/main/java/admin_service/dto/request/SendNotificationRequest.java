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
public class SendNotificationRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must be at most 2000 characters")
    private String message;

    /** Target user ID for individual notifications. Null for broadcast. */
    private Long targetUserId;

    /** Target department for department-wide notifications. */
    private String department;

    /** Target faculty for faculty-wide notifications. */
    private String faculty;

    /** Notification type identifier. */
    private String notificationType;
}

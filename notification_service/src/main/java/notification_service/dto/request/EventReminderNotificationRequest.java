package notification_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReminderNotificationRequest {

    @NotNull
    private Long receiverUserId;

    @NotBlank
    private String eventId;

    @NotBlank
    private String eventTitle;

    @NotNull
    private LocalDateTime eventStartDateTime;
}

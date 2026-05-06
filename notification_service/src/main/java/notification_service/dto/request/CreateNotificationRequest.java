package notification_service.dto.request;

import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateNotificationRequest {

    @NotNull
    private Long receiverUserId;

    private Long senderUserId;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationPriority priority;

    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String message;

    @NotNull
    private TargetType targetType;

    @NotBlank
    @Size(max = 120)
    private String targetId;

    @Size(max = 400)
    private String actionUrl;

    @Size(max = 400)
    private String imageUrl;

    private String metadata;

    private LocalDateTime expiresAt;
}

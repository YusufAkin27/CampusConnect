package notification_service.dto.response;

import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
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
public class NotificationResponse {

    private Long id;
    private Long receiverUserId;
    private Long senderUserId;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationPriority priority;
    private String title;
    private String message;
    private TargetType targetType;
    private String targetId;
    private String actionUrl;
    private String imageUrl;
    private boolean read;
    private LocalDateTime readAt;
    private boolean delivered;
    private LocalDateTime deliveredAt;
    private String metadata;
    private LocalDateTime createdAt;
}

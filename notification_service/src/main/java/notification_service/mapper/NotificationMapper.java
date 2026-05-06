package notification_service.mapper;

import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.response.NotificationPreferenceResponse;
import notification_service.dto.response.NotificationResponse;
import notification_service.entity.Notification;
import notification_service.entity.NotificationPreference;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequest request) {
        return Notification.builder()
            .receiverUserId(request.getReceiverUserId())
            .senderUserId(request.getSenderUserId())
            .type(request.getType())
            .channel(request.getChannel())
            .priority(request.getPriority())
            .title(request.getTitle())
            .message(request.getMessage())
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .actionUrl(request.getActionUrl())
            .imageUrl(request.getImageUrl())
            .metadata(request.getMetadata())
            .expiresAt(request.getExpiresAt())
            .read(false)
            .delivered(false)
            .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .receiverUserId(notification.getReceiverUserId())
            .senderUserId(notification.getSenderUserId())
            .type(notification.getType())
            .channel(notification.getChannel())
            .priority(notification.getPriority())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .targetType(notification.getTargetType())
            .targetId(notification.getTargetId())
            .actionUrl(notification.getActionUrl())
            .imageUrl(notification.getImageUrl())
            .read(notification.isRead())
            .readAt(notification.getReadAt())
            .delivered(notification.isDelivered())
            .deliveredAt(notification.getDeliveredAt())
            .metadata(notification.getMetadata())
            .createdAt(notification.getCreatedAt())
            .build();
    }

    public NotificationPreferenceResponse toPreferenceResponse(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
            .userId(preference.getUserId())
            .inAppEnabled(preference.isInAppEnabled())
            .pushEnabled(preference.isPushEnabled())
            .emailEnabled(preference.isEmailEnabled())
            .chatNotificationsEnabled(preference.isChatNotificationsEnabled())
            .eventNotificationsEnabled(preference.isEventNotificationsEnabled())
            .friendNotificationsEnabled(preference.isFriendNotificationsEnabled())
            .postNotificationsEnabled(preference.isPostNotificationsEnabled())
            .marketingNotificationsEnabled(preference.isMarketingNotificationsEnabled())
            .quietHoursEnabled(preference.isQuietHoursEnabled())
            .quietHoursStart(preference.getQuietHoursStart())
            .quietHoursEnd(preference.getQuietHoursEnd())
            .build();
    }
}

package notification_service.controller;

import notification_service.dto.request.BasicNotificationRequest;
import notification_service.dto.request.ChatMessageNotificationRequest;
import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.request.EventReminderNotificationRequest;
import notification_service.dto.response.ApiResponse;
import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
import notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/notifications/internal")
public class NotificationInternalController {

    private final NotificationService notificationService;

    @PostMapping("/post-liked")
    public ApiResponse<Void> postLiked(@Valid @RequestBody BasicNotificationRequest request) {
        notificationService.createNotification(buildBasic(request, NotificationType.POST_LIKED, TargetType.POST));
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/post-commented")
    public ApiResponse<Void> postCommented(@Valid @RequestBody BasicNotificationRequest request) {
        notificationService.createNotification(buildBasic(request, NotificationType.POST_COMMENTED, TargetType.POST));
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/friend-request")
    public ApiResponse<Void> friendRequest(@Valid @RequestBody BasicNotificationRequest request) {
        notificationService.createNotification(buildBasic(request, NotificationType.FRIEND_REQUEST_RECEIVED, TargetType.USER));
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/event-reminder")
    public ApiResponse<Void> eventReminder(@Valid @RequestBody EventReminderNotificationRequest request) {
        CreateNotificationRequest create = CreateNotificationRequest.builder()
            .receiverUserId(request.getReceiverUserId())
            .type(NotificationType.EVENT_REMINDER)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.HIGH)
            .title("Event Reminder")
            .message(request.getEventTitle())
            .targetType(TargetType.EVENT)
            .targetId(request.getEventId())
            .build();
        notificationService.createNotification(create);
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/chat-message")
    public ApiResponse<Void> chatMessage(@Valid @RequestBody ChatMessageNotificationRequest request) {
        CreateNotificationRequest create = CreateNotificationRequest.builder()
            .receiverUserId(request.getReceiverUserId())
            .senderUserId(request.getSenderUserId())
            .type(NotificationType.CHAT_MESSAGE_RECEIVED)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.NORMAL)
            .title("New message")
            .message(request.getMessagePreview())
            .targetType(TargetType.CHAT)
            .targetId(request.getConversationId())
            .build();
        notificationService.createNotification(create);
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/chat-mention")
    public ApiResponse<Void> chatMention(@Valid @RequestBody ChatMessageNotificationRequest request) {
        CreateNotificationRequest create = CreateNotificationRequest.builder()
            .receiverUserId(request.getReceiverUserId())
            .senderUserId(request.getSenderUserId())
            .type(NotificationType.CHAT_MENTION)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.HIGH)
            .title("Mention")
            .message(request.getMessagePreview())
            .targetType(TargetType.CHAT)
            .targetId(request.getConversationId())
            .build();
        notificationService.createNotification(create);
        return ApiResponse.success("Notification created", null);
    }

    @PostMapping("/system-announcement")
    public ApiResponse<Void> systemAnnouncement(@Valid @RequestBody BasicNotificationRequest request) {
        notificationService.createNotification(buildBasic(request, NotificationType.SYSTEM_ANNOUNCEMENT, TargetType.SYSTEM));
        return ApiResponse.success("Notification created", null);
    }

    private CreateNotificationRequest buildBasic(BasicNotificationRequest request, NotificationType type, TargetType targetType) {
        return CreateNotificationRequest.builder()
            .receiverUserId(request.getReceiverUserId())
            .senderUserId(request.getSenderUserId())
            .type(type)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.NORMAL)
            .title(request.getTitle())
            .message(request.getMessage())
            .targetType(targetType)
            .targetId(request.getTargetId())
            .actionUrl(request.getActionUrl())
            .build();
    }
}

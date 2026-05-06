package notification_service.service;

import notification_service.client.LoggingClient;
import notification_service.client.UserClient;
import notification_service.client.dto.LogEventRequest;
import notification_service.dto.request.BulkNotificationRequest;
import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.request.UpdateNotificationPreferenceRequest;
import notification_service.dto.response.NotificationPreferenceResponse;
import notification_service.dto.response.NotificationResponse;
import notification_service.dto.response.UnreadCountResponse;
import notification_service.entity.Notification;
import notification_service.entity.NotificationDeliveryLog;
import notification_service.entity.NotificationPreference;
import notification_service.enums.DeliveryStatus;
import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.exception.ExternalServiceUnavailableException;
import notification_service.exception.NotificationAccessDeniedException;
import notification_service.exception.NotificationNotFoundException;
import notification_service.mapper.NotificationMapper;
import notification_service.repository.NotificationDeliveryLogRepository;
import notification_service.repository.NotificationPreferenceRepository;
import notification_service.repository.NotificationRepository;
import notification_service.sender.EmailNotificationSender;
import notification_service.sender.InAppNotificationSender;
import notification_service.sender.PushNotificationSender;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationDeliveryLogRepository deliveryLogRepository;
    private final NotificationMapper notificationMapper;
    private final UserClient userClient;
    private final LoggingClient loggingClient;
    private final InAppNotificationSender inAppSender;
    private final PushNotificationSender pushSender;
    private final EmailNotificationSender emailSender;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        validateReceiver(request.getReceiverUserId());
        NotificationPreference preference = getOrCreatePreferences(request.getReceiverUserId());
        if (!isAllowedByPreference(preference, request.getType(), request.getChannel())) {
            return null;
        }
        if (isQuietHours(preference)) {
            return null;
        }

        Notification notification = notificationMapper.toEntity(request);
        Notification saved = notificationRepository.save(notification);

        createDeliveryLog(saved, DeliveryStatus.PENDING, null, null);
        deliver(saved, preference);

        logEvent("NOTIFICATION_CREATED", saved.getReceiverUserId(), String.valueOf(saved.getId()), "Notification created");

        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void createBulkNotification(BulkNotificationRequest request) {
        for (Long receiverId : request.getReceiverUserIds()) {
            CreateNotificationRequest single = CreateNotificationRequest.builder()
                .receiverUserId(receiverId)
                .senderUserId(null)
                .type(request.getType())
                .channel(request.getChannel())
                .priority(request.getPriority())
                .title(request.getTitle())
                .message(request.getMessage())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .actionUrl(request.getActionUrl())
                .metadata(request.getMetadata())
                .build();
            createNotification(single);
        }
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(Long userId, Boolean read, NotificationType type,
                                                         NotificationPriority priority, Pageable pageable) {
        Page<Notification> page;
        if (read != null) {
            page = notificationRepository.findByReceiverUserIdAndRead(userId, read, pageable);
        } else if (type != null) {
            page = notificationRepository.findByReceiverUserIdAndType(userId, type, pageable);
        } else if (priority != null) {
            page = notificationRepository.findByReceiverUserIdAndPriority(userId, priority, pageable);
        } else {
            page = notificationRepository.findByReceiverUserId(userId, pageable);
        }
        List<NotificationResponse> responses = page.getContent().stream()
            .filter(this::notExpired)
            .map(notificationMapper::toResponse)
            .toList();
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByReceiverUserIdAndRead(userId, false, pageable);
        List<NotificationResponse> responses = page.getContent().stream()
            .filter(this::notExpired)
            .map(notificationMapper::toResponse)
            .toList();
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Override
    public UnreadCountResponse getUnreadCount(Long userId) {
        long count = notificationRepository.countByReceiverUserIdAndRead(userId, false);
        return UnreadCountResponse.builder().unreadCount(count).build();
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        if (!notification.getReceiverUserId().equals(userId)) {
            throw new NotificationAccessDeniedException("Notification access denied");
        }
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
        logEvent("NOTIFICATION_READ", userId, String.valueOf(notificationId), "Notification read");
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Page<Notification> page = notificationRepository.findByReceiverUserIdAndRead(userId, false, Pageable.unpaged());
        for (Notification notification : page.getContent()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(page.getContent());
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        if (!notification.getReceiverUserId().equals(userId)) {
            throw new NotificationAccessDeniedException("Notification access denied");
        }
        notificationRepository.delete(notification);
        logEvent("NOTIFICATION_DELETED", userId, String.valueOf(notificationId), "Notification deleted");
    }

    @Override
    @Transactional
    public void clearMyNotifications(Long userId) {
        notificationRepository.deleteByReceiverUserId(userId);
    }

    @Override
    public NotificationPreferenceResponse getPreferences(Long userId) {
        NotificationPreference preference = getOrCreatePreferences(userId);
        return notificationMapper.toPreferenceResponse(preference);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse updatePreferences(Long userId, UpdateNotificationPreferenceRequest request) {
        NotificationPreference preference = getOrCreatePreferences(userId);
        if (request.getInAppEnabled() != null) {
            preference.setInAppEnabled(request.getInAppEnabled());
        }
        if (request.getPushEnabled() != null) {
            preference.setPushEnabled(request.getPushEnabled());
        }
        if (request.getEmailEnabled() != null) {
            preference.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getChatNotificationsEnabled() != null) {
            preference.setChatNotificationsEnabled(request.getChatNotificationsEnabled());
        }
        if (request.getEventNotificationsEnabled() != null) {
            preference.setEventNotificationsEnabled(request.getEventNotificationsEnabled());
        }
        if (request.getFriendNotificationsEnabled() != null) {
            preference.setFriendNotificationsEnabled(request.getFriendNotificationsEnabled());
        }
        if (request.getPostNotificationsEnabled() != null) {
            preference.setPostNotificationsEnabled(request.getPostNotificationsEnabled());
        }
        if (request.getMarketingNotificationsEnabled() != null) {
            preference.setMarketingNotificationsEnabled(request.getMarketingNotificationsEnabled());
        }
        if (request.getQuietHoursEnabled() != null) {
            preference.setQuietHoursEnabled(request.getQuietHoursEnabled());
        }
        if (request.getQuietHoursStart() != null) {
            preference.setQuietHoursStart(request.getQuietHoursStart());
        }
        if (request.getQuietHoursEnd() != null) {
            preference.setQuietHoursEnd(request.getQuietHoursEnd());
        }
        NotificationPreference saved = preferenceRepository.save(preference);
        logEvent("NOTIFICATION_PREFERENCES_UPDATED", userId, null, "Preferences updated");
        return notificationMapper.toPreferenceResponse(saved);
    }

    private void validateReceiver(Long receiverUserId) {
        try {
            UserClient.ExistsResponse response = userClient.existsUser(receiverUserId);
            if (response == null || !response.exists) {
                throw new NotificationNotFoundException("Receiver user not found");
            }
        } catch (NotificationNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalServiceUnavailableException("User service unavailable");
        }
    }

    private NotificationPreference getOrCreatePreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
            .orElseGet(() -> preferenceRepository.save(defaultPreferences(userId)));
    }

    private NotificationPreference defaultPreferences(Long userId) {
        return NotificationPreference.builder()
            .userId(userId)
            .inAppEnabled(true)
            .pushEnabled(true)
            .emailEnabled(true)
            .chatNotificationsEnabled(true)
            .eventNotificationsEnabled(true)
            .friendNotificationsEnabled(true)
            .postNotificationsEnabled(true)
            .marketingNotificationsEnabled(false)
            .quietHoursEnabled(false)
            .build();
    }

    private boolean isAllowedByPreference(NotificationPreference preference, NotificationType type,
                                          NotificationChannel channel) {
        boolean channelAllowed = switch (channel) {
            case IN_APP -> preference.isInAppEnabled();
            case PUSH -> preference.isPushEnabled();
            case EMAIL -> preference.isEmailEnabled();
            case SMS -> false;
        };
        if (!channelAllowed) {
            return false;
        }
        return switch (type) {
            case CHAT_MESSAGE_RECEIVED, CHAT_GROUP_INVITE, CHAT_MENTION -> preference.isChatNotificationsEnabled();
            case EVENT_CREATED, EVENT_REMINDER, EVENT_CANCELLED -> preference.isEventNotificationsEnabled();
            case FRIEND_REQUEST_ACCEPTED, FRIEND_REQUEST_RECEIVED -> preference.isFriendNotificationsEnabled();
            case POST_LIKED, POST_COMMENTED, COMMENT_REPLIED -> preference.isPostNotificationsEnabled();
            default -> true;
        };
    }

    private boolean isQuietHours(NotificationPreference preference) {
        if (!preference.isQuietHoursEnabled()) {
            return false;
        }
        LocalTime start = preference.getQuietHoursStart();
        LocalTime end = preference.getQuietHoursEnd();
        if (start == null || end == null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        if (start.isBefore(end)) {
            return now.isAfter(start) && now.isBefore(end);
        }
        return now.isAfter(start) || now.isBefore(end);
    }

    private boolean notExpired(Notification notification) {
        return notification.getExpiresAt() == null || notification.getExpiresAt().isAfter(LocalDateTime.now());
    }

    private void deliver(Notification notification, NotificationPreference preference) {
        try {
            switch (notification.getChannel()) {
                case IN_APP -> inAppSender.send(notification);
                case PUSH -> pushSender.send(notification);
                case EMAIL -> emailSender.send(notification);
                case SMS -> {
                }
            }
            notification.setDelivered(true);
            notification.setDeliveredAt(LocalDateTime.now());
            notificationRepository.save(notification);
            updateDeliveryLog(notification.getId(), DeliveryStatus.SENT, null, null);
            logEvent("NOTIFICATION_SENT", notification.getReceiverUserId(), String.valueOf(notification.getId()),
                "Notification sent");
        } catch (Exception ex) {
            updateDeliveryLog(notification.getId(), DeliveryStatus.FAILED, null, ex.getMessage());
            logEvent("NOTIFICATION_FAILED", notification.getReceiverUserId(), String.valueOf(notification.getId()),
                "Notification failed");
        }
    }

    private void createDeliveryLog(Notification notification, DeliveryStatus status, String response, String error) {
        NotificationDeliveryLog log = NotificationDeliveryLog.builder()
            .notificationId(notification.getId())
            .channel(notification.getChannel())
            .status(status)
            .providerResponse(response)
            .errorMessage(error)
            .retryCount(0)
            .lastTriedAt(LocalDateTime.now())
            .build();
        deliveryLogRepository.save(log);
    }

    private void updateDeliveryLog(Long notificationId, DeliveryStatus status, String response, String error) {
        deliveryLogRepository.findTopByNotificationIdOrderByCreatedAtDesc(notificationId)
            .ifPresent(log -> {
                log.setStatus(status);
                log.setProviderResponse(response);
                log.setErrorMessage(error);
                log.setLastTriedAt(LocalDateTime.now());
                deliveryLogRepository.save(log);
            });
    }

    

    private void logEvent(String type, Long actorUserId, String referenceId, String message) {
        try {
            loggingClient.logEvent(LogEventRequest.builder()
                .eventType(type)
                .actorUserId(actorUserId)
                .referenceId(referenceId)
                .message(message)
                .metadata(Map.of("service", "notification-service"))
                .timestamp(LocalDateTime.now())
                .build());
        } catch (Exception ex) {
            log.warn("Logging service failed", ex);
        }
    }
}

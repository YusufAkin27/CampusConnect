package notification_service.service;

import notification_service.dto.request.BulkNotificationRequest;
import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.request.UpdateNotificationPreferenceRequest;
import notification_service.dto.response.NotificationPreferenceResponse;
import notification_service.dto.response.NotificationResponse;
import notification_service.dto.response.UnreadCountResponse;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse createNotification(CreateNotificationRequest request);

    void createBulkNotification(BulkNotificationRequest request);

    Page<NotificationResponse> getMyNotifications(Long userId, Boolean read, NotificationType type,
                                                  NotificationPriority priority, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable);

    UnreadCountResponse getUnreadCount(Long userId);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long userId, Long notificationId);

    void clearMyNotifications(Long userId);

    NotificationPreferenceResponse getPreferences(Long userId);

    NotificationPreferenceResponse updatePreferences(Long userId, UpdateNotificationPreferenceRequest request);
}

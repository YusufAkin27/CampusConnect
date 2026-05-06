package notification_service.service;

import notification_service.client.LoggingClient;
import notification_service.client.UserClient;
import notification_service.client.dto.LogEventRequest;
import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.response.NotificationResponse;
import notification_service.dto.response.UnreadCountResponse;
import notification_service.entity.Notification;
import notification_service.entity.NotificationPreference;
import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
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
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    private NotificationPreferenceRepository preferenceRepository = Mockito.mock(NotificationPreferenceRepository.class);
    private NotificationDeliveryLogRepository deliveryLogRepository = Mockito.mock(NotificationDeliveryLogRepository.class);
    private NotificationMapper mapper = new NotificationMapper();
    private UserClient userClient = Mockito.mock(UserClient.class);
    private LoggingClient loggingClient = Mockito.mock(LoggingClient.class);
    private InAppNotificationSender inAppSender = Mockito.mock(InAppNotificationSender.class);
    private PushNotificationSender pushSender = Mockito.mock(PushNotificationSender.class);
    private EmailNotificationSender emailSender = Mockito.mock(EmailNotificationSender.class);

    private NotificationServiceImpl service = new NotificationServiceImpl(
        notificationRepository,
        preferenceRepository,
        deliveryLogRepository,
        mapper,
        userClient,
        loggingClient,
        inAppSender,
        pushSender,
        emailSender
    );

    @Test
    void createNotification() {
        CreateNotificationRequest request = baseRequest();
        UserClient.ExistsResponse exists = new UserClient.ExistsResponse();
        exists.exists = true;
        Mockito.when(userClient.existsUser(1L)).thenReturn(exists);
        Mockito.when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(defaultPreference(1L)));
        Mockito.when(notificationRepository.save(Mockito.any(Notification.class))).thenAnswer(inv -> {
            Notification notification = inv.getArgument(0, Notification.class);
            notification.setId(10L);
            return notification;
        });

        NotificationResponse response = service.createNotification(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(10L, response.getId());
    }

    @Test
    void preferenceDisabledSkipsNotification() {
        CreateNotificationRequest request = baseRequest();
        UserClient.ExistsResponse exists = new UserClient.ExistsResponse();
        exists.exists = true;
        Mockito.when(userClient.existsUser(1L)).thenReturn(exists);
        NotificationPreference pref = defaultPreference(1L);
        pref.setInAppEnabled(false);
        Mockito.when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(pref));

        NotificationResponse response = service.createNotification(request);
        Assertions.assertNull(response);
        Mockito.verify(notificationRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void unreadCount() {
        Mockito.when(notificationRepository.countByReceiverUserIdAndRead(1L, false)).thenReturn(5L);
        UnreadCountResponse response = service.getUnreadCount(1L);
        Assertions.assertEquals(5L, response.getUnreadCount());
    }

    @Test
    void markAsRead() {
        Notification notification = Notification.builder().id(1L).receiverUserId(2L).build();
        Mockito.when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Assertions.assertThrows(RuntimeException.class, () -> service.markAsRead(1L, 1L));
    }

    @Test
    void markAllAsRead() {
        Notification notification = Notification.builder().id(1L).receiverUserId(1L).read(false).build();
        Mockito.when(notificationRepository.findByReceiverUserIdAndRead(Mockito.eq(1L), Mockito.eq(false), Mockito.any()))
            .thenReturn(new PageImpl<>(List.of(notification)));

        service.markAllAsRead(1L);
        Assertions.assertTrue(notification.isRead());
    }

    @Test
    void deleteNotification() {
        Notification notification = Notification.builder().id(1L).receiverUserId(1L).build();
        Mockito.when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        service.deleteNotification(1L, 1L);
        Mockito.verify(notificationRepository).delete(notification);
    }

    @Test
    void quietHoursSkipsNotification() {
        CreateNotificationRequest request = baseRequest();
        UserClient.ExistsResponse exists = new UserClient.ExistsResponse();
        exists.exists = true;
        Mockito.when(userClient.existsUser(1L)).thenReturn(exists);
        NotificationPreference pref = defaultPreference(1L);
        pref.setQuietHoursEnabled(true);
        pref.setQuietHoursStart(LocalTime.now().minusMinutes(10));
        pref.setQuietHoursEnd(LocalTime.now().plusMinutes(10));
        Mockito.when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(pref));

        NotificationResponse response = service.createNotification(request);
        Assertions.assertNull(response);
    }

    @Test
    void expiredNotificationFiltered() {
        Notification notification = Notification.builder()
            .id(1L)
            .receiverUserId(1L)
            .expiresAt(LocalDateTime.now().minusDays(1))
            .build();
        Mockito.when(notificationRepository.findByReceiverUserId(Mockito.eq(1L), Mockito.any()))
            .thenReturn(new PageImpl<>(List.of(notification), PageRequest.of(0, 10), 1));

        var page = service.getMyNotifications(1L, null, null, null, PageRequest.of(0, 10));
        Assertions.assertTrue(page.getContent().isEmpty());
    }

    private CreateNotificationRequest baseRequest() {
        return CreateNotificationRequest.builder()
            .receiverUserId(1L)
            .type(NotificationType.CHAT_MESSAGE_RECEIVED)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.NORMAL)
            .title("Title")
            .message("Message")
            .targetType(TargetType.CHAT)
            .targetId("c1")
            .build();
    }

    private NotificationPreference defaultPreference(Long userId) {
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
}

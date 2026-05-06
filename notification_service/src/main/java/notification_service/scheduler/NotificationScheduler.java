package notification_service.scheduler;

import notification_service.entity.Notification;
import notification_service.entity.NotificationDeliveryLog;
import notification_service.enums.DeliveryStatus;
import notification_service.repository.NotificationDeliveryLogRepository;
import notification_service.repository.NotificationRepository;
import notification_service.sender.EmailNotificationSender;
import notification_service.sender.InAppNotificationSender;
import notification_service.sender.PushNotificationSender;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryLogRepository deliveryLogRepository;
    private final InAppNotificationSender inAppSender;
    private final PushNotificationSender pushSender;
    private final EmailNotificationSender emailSender;

    @Value("${notification.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${notification.expire.cleanup-days:30}")
    private int cleanupDays;

    @Scheduled(fixedDelayString = "600000")
    @Transactional
    public void cleanupExpired() {
        LocalDateTime now = LocalDateTime.now();
        long expiredDeleted = notificationRepository.deleteByExpiresAtBefore(now);
        LocalDateTime cutoff = now.minusDays(cleanupDays);
        long oldDeleted = notificationRepository.deleteByCreatedAtBefore(cutoff);
        log.info("Expired notifications cleanup, expired {}, old {}", expiredDeleted, oldDeleted);
    }

    @Scheduled(fixedDelayString = "300000")
    @Transactional
    public void retryFailedDeliveries() {
        List<NotificationDeliveryLog> logs = deliveryLogRepository.findByStatusIn(
            List.of(DeliveryStatus.FAILED, DeliveryStatus.RETRYING, DeliveryStatus.PENDING));
        for (NotificationDeliveryLog logEntry : logs) {
            if (logEntry.getRetryCount() >= maxAttempts) {
                logEntry.setStatus(DeliveryStatus.CANCELLED);
                deliveryLogRepository.save(logEntry);
                continue;
            }
            Notification notification = notificationRepository.findById(logEntry.getNotificationId()).orElse(null);
            if (notification == null) {
                logEntry.setStatus(DeliveryStatus.CANCELLED);
                deliveryLogRepository.save(logEntry);
                continue;
            }
            try {
                switch (notification.getChannel()) {
                    case IN_APP -> inAppSender.send(notification);
                    case PUSH -> pushSender.send(notification);
                    case EMAIL -> emailSender.send(notification);
                    case SMS -> {
                    }
                }
                logEntry.setStatus(DeliveryStatus.SENT);
                notification.setDelivered(true);
                notification.setDeliveredAt(LocalDateTime.now());
                notificationRepository.save(notification);
            } catch (Exception ex) {
                logEntry.setStatus(DeliveryStatus.RETRYING);
                logEntry.setErrorMessage(ex.getMessage());
            }
            logEntry.setRetryCount(logEntry.getRetryCount() + 1);
            logEntry.setLastTriedAt(LocalDateTime.now());
            deliveryLogRepository.save(logEntry);
        }
    }
}

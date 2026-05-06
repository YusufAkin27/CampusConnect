package notification_service.sender;

import notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("Push notification simulated for user {}", notification.getReceiverUserId());
    }
}

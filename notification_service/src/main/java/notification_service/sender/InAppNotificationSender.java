package notification_service.sender;

import notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InAppNotificationSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("In-app notification queued for user {}", notification.getReceiverUserId());
    }
}

package notification_service.sender;

import notification_service.entity.Notification;

public interface NotificationSender {

    void send(Notification notification);
}

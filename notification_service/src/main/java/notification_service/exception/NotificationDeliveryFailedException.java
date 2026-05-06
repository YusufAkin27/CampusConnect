package notification_service.exception;

public class NotificationDeliveryFailedException extends RuntimeException {
    public NotificationDeliveryFailedException(String message) {
        super(message);
    }
}

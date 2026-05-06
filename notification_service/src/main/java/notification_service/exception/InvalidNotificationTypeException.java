package notification_service.exception;

public class InvalidNotificationTypeException extends RuntimeException {
    public InvalidNotificationTypeException(String message) {
        super(message);
    }
}

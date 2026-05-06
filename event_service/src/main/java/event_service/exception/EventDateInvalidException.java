package event_service.exception;

public class EventDateInvalidException extends RuntimeException {
    public EventDateInvalidException(String message) {
        super(message);
    }
}

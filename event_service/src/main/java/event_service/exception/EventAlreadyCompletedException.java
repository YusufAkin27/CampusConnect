package event_service.exception;

public class EventAlreadyCompletedException extends RuntimeException {
    public EventAlreadyCompletedException(String message) {
        super(message);
    }
}

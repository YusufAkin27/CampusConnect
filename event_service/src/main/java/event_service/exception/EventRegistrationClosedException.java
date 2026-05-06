package event_service.exception;

public class EventRegistrationClosedException extends RuntimeException {
    public EventRegistrationClosedException(String message) {
        super(message);
    }
}

package event_service.exception;

public class EventAlreadyJoinedException extends RuntimeException {
    public EventAlreadyJoinedException(String message) {
        super(message);
    }
}

package event_service.exception;

public class EventNotJoinedException extends RuntimeException {
    public EventNotJoinedException(String message) {
        super(message);
    }
}

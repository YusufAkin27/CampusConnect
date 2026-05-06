package event_service.exception;

public class EventPermissionDeniedException extends RuntimeException {
    public EventPermissionDeniedException(String message) {
        super(message);
    }
}

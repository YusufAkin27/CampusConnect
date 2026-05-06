package event_service.exception;

public class EventCapacityFullException extends RuntimeException {
    public EventCapacityFullException(String message) {
        super(message);
    }
}

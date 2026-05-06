package event_service.exception;

public class MediaValidationFailedException extends RuntimeException {
    public MediaValidationFailedException(String message) {
        super(message);
    }
}

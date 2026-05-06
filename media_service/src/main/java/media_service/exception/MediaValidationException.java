package media_service.exception;

public class MediaValidationException extends RuntimeException {
    public MediaValidationException(String message) {
        super(message);
    }
}

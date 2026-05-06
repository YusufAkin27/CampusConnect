package media_service.exception;

public class InvalidMediaUsageException extends RuntimeException {
    public InvalidMediaUsageException(String message) {
        super(message);
    }
}

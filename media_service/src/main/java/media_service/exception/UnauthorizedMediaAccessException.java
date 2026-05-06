package media_service.exception;

public class UnauthorizedMediaAccessException extends RuntimeException {
    public UnauthorizedMediaAccessException(String message) {
        super(message);
    }
}

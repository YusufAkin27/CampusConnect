package logging_service.exception;

public class UnauthorizedLogAccessException extends RuntimeException {
    public UnauthorizedLogAccessException(String message) {
        super(message);
    }
}

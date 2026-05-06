package media_service.exception;

public class UnauthorizedUserOperationException extends RuntimeException {
    public UnauthorizedUserOperationException(String message) {
        super(message);
    }
}

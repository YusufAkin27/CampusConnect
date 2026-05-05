package post_service.exception;

public class UnauthorizedUserOperationException extends RuntimeException {
    public UnauthorizedUserOperationException(String message) {
        super(message);
    }
    public UnauthorizedUserOperationException() {
        super("Unauthorized: Missing or invalid authentication information.");
    }
}

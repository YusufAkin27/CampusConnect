package admin_service.exception;

public class UnauthorizedAdminActionException extends RuntimeException {
    public UnauthorizedAdminActionException(String message) {
        super(message);
    }
}

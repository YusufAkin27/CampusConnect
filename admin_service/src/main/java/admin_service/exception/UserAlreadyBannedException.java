package admin_service.exception;

public class UserAlreadyBannedException extends RuntimeException {
    public UserAlreadyBannedException(String message) {
        super(message);
    }
}

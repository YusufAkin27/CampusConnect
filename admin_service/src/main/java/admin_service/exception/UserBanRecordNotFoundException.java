package admin_service.exception;

public class UserBanRecordNotFoundException extends RuntimeException {
    public UserBanRecordNotFoundException(String message) {
        super(message);
    }
}

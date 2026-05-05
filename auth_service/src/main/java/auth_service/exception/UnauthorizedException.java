package auth_service.exception;

public class UnauthorizedException extends AuthException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Bu işlem için yetkiniz bulunmamaktadır.");
    }
}

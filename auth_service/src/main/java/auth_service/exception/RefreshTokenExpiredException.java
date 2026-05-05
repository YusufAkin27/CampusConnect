package auth_service.exception;

public class RefreshTokenExpiredException extends AuthException {

    public RefreshTokenExpiredException(String message) {
        super(message);
    }

    public RefreshTokenExpiredException() {
        super("Refresh token süresi dolmuş. Lütfen tekrar giriş yapınız.");
    }
}

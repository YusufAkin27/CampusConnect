package auth_service.exception;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Geçersiz token. Lütfen tekrar giriş yapınız.");
    }
}

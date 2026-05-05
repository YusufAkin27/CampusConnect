package auth_service.exception;

public class InvalidPasswordException extends AuthException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException() {
        super("Şifre hatalı. Lütfen tekrar deneyiniz.");
    }
}

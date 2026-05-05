package auth_service.exception;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException withUsername(String username) {
        return new UserAlreadyExistsException("Kullanıcı adı zaten kullanımda: " + username);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        return new UserAlreadyExistsException("Email adresi zaten kayıtlı: " + email);
    }
}

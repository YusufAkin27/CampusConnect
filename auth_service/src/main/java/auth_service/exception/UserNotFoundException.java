package auth_service.exception;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException withIdentifier(String identifier) {
        return new UserNotFoundException("Kullanıcı bulunamadı: " + identifier);
    }

    public static UserNotFoundException withId(Long id) {
        return new UserNotFoundException("Kullanıcı bulunamadı. ID: " + id);
    }
}

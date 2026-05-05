package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }

    public UserProfileNotFoundException(Long id) {
        super("User profile not found with id: " + id);
    }
}

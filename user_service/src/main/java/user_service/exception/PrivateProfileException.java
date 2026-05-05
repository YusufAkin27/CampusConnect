package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PrivateProfileException extends RuntimeException {

    public PrivateProfileException(String username) {
        super("Profile is private and cannot be viewed: " + username);
    }
}

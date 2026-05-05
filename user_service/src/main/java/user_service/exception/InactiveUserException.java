package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InactiveUserException extends RuntimeException {

    public InactiveUserException(String username) {
        super("User account is not active: " + username);
    }
}

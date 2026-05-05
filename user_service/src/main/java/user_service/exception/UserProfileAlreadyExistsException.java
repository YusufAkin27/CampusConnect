package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserProfileAlreadyExistsException extends RuntimeException {

    public UserProfileAlreadyExistsException(Long authUserId) {
        super("User profile already exists for authUserId: " + authUserId);
    }
}

package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProfileDataException extends RuntimeException {

    public InvalidProfileDataException(String message) {
        super(message);
    }
}

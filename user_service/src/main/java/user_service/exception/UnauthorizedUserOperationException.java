package user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedUserOperationException extends RuntimeException {

    public UnauthorizedUserOperationException(String message) {
        super(message);
    }
}

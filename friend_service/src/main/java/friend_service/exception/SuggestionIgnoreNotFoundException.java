package friend_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SuggestionIgnoreNotFoundException extends RuntimeException {
    public SuggestionIgnoreNotFoundException(String message) {
        super(message);
    }
}

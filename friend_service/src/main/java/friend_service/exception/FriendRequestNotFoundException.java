package friend_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FriendRequestNotFoundException extends RuntimeException {
    public FriendRequestNotFoundException(String message) {
        super(message);
    }
    public FriendRequestNotFoundException(Long requestId) {
        super("Friend request not found with id: " + requestId);
    }
}

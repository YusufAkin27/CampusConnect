package story_service.exception;

public class UnauthorizedStoryActionException extends RuntimeException {
    public UnauthorizedStoryActionException(String message) {
        super(message);
    }
}

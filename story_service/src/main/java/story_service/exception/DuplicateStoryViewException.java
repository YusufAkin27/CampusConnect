package story_service.exception;

public class DuplicateStoryViewException extends RuntimeException {
    public DuplicateStoryViewException(String message) {
        super(message);
    }
}

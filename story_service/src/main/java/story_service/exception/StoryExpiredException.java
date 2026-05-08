package story_service.exception;

public class StoryExpiredException extends RuntimeException {
    public StoryExpiredException(String message) {
        super(message);
    }
}

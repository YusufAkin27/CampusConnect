package story_service.exception;

public class StoryAlreadyDeletedException extends RuntimeException {
    public StoryAlreadyDeletedException(String message) {
        super(message);
    }
}

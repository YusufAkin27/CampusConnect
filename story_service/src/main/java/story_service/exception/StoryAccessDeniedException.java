package story_service.exception;

public class StoryAccessDeniedException extends RuntimeException {
    public StoryAccessDeniedException(String message) {
        super(message);
    }
}

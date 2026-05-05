package post_service.exception;

public class PostAccessDeniedException extends RuntimeException {
    public PostAccessDeniedException(String message) {
        super(message);
    }
    public PostAccessDeniedException() {
        super("You do not have permission to perform this action on the post.");
    }
}

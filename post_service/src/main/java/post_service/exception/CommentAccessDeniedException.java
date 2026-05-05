package post_service.exception;

public class CommentAccessDeniedException extends RuntimeException {
    public CommentAccessDeniedException(String message) {
        super(message);
    }
    public CommentAccessDeniedException() {
        super("You do not have permission to perform this action on the comment.");
    }
}

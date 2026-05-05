package post_service.exception;

public class InactiveCommentException extends RuntimeException {
    public InactiveCommentException(Long commentId) {
        super("Comment is not active: " + commentId);
    }
    public InactiveCommentException(String message) {
        super(message);
    }
}

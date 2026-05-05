package post_service.exception;

public class InactivePostException extends RuntimeException {
    public InactivePostException(Long postId) {
        super("Post is not active: " + postId);
    }
    public InactivePostException(String message) {
        super(message);
    }
}

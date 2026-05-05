package post_service.exception;

public class CommentsDisabledException extends RuntimeException {
    public CommentsDisabledException() {
        super("Comments are disabled for this post.");
    }
}

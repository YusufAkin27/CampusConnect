package post_service.exception;

public class CommentAlreadyLikedException extends RuntimeException {
    public CommentAlreadyLikedException() {
        super("You have already reacted to this comment.");
    }
}

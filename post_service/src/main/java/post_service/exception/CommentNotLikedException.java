package post_service.exception;

public class CommentNotLikedException extends RuntimeException {
    public CommentNotLikedException() {
        super("You have not reacted to this comment.");
    }
}

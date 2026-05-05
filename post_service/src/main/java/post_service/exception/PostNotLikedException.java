package post_service.exception;

public class PostNotLikedException extends RuntimeException {
    public PostNotLikedException() {
        super("You have not reacted to this post.");
    }
}

package post_service.exception;

public class PostAlreadyLikedException extends RuntimeException {
    public PostAlreadyLikedException() {
        super("You have already reacted to this post.");
    }
}

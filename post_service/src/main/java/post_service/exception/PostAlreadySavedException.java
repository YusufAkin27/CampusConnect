package post_service.exception;

public class PostAlreadySavedException extends RuntimeException {
    public PostAlreadySavedException() {
        super("You have already saved this post.");
    }
}

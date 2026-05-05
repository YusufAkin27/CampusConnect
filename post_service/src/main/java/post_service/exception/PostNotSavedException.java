package post_service.exception;

public class PostNotSavedException extends RuntimeException {
    public PostNotSavedException() {
        super("You have not saved this post.");
    }
}

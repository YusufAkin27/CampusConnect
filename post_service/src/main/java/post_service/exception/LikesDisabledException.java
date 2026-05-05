package post_service.exception;

public class LikesDisabledException extends RuntimeException {
    public LikesDisabledException() {
        super("Likes/reactions are disabled for this post.");
    }
}

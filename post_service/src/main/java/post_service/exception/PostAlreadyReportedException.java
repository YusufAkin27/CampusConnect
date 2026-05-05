package post_service.exception;

public class PostAlreadyReportedException extends RuntimeException {
    public PostAlreadyReportedException() {
        super("You have already reported this post.");
    }
}

package post_service.exception;

public class CommentAlreadyReportedException extends RuntimeException {
    public CommentAlreadyReportedException() {
        super("You have already reported this comment.");
    }
}

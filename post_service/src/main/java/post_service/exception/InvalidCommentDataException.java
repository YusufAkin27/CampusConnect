package post_service.exception;

public class InvalidCommentDataException extends RuntimeException {
    public InvalidCommentDataException(String message) {
        super(message);
    }
}

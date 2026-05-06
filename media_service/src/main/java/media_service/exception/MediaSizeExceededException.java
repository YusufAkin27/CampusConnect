package media_service.exception;

public class MediaSizeExceededException extends RuntimeException {
    public MediaSizeExceededException(String message) {
        super(message);
    }
}

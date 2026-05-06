package media_service.exception;

public class MediaUsageNotFoundException extends RuntimeException {
    public MediaUsageNotFoundException(String message) {
        super(message);
    }
}

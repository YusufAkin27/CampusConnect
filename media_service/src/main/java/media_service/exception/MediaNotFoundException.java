package media_service.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException(Long mediaId) {
        super("Media not found with ID: " + mediaId);
    }
    public MediaNotFoundException(String message) {
        super(message);
    }
}

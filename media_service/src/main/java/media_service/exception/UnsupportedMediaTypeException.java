package media_service.exception;

public class UnsupportedMediaTypeException extends RuntimeException {
    public UnsupportedMediaTypeException(String mimeType) {
        super("Unsupported media type: " + mimeType + ". Please use an allowed file type.");
    }
    public UnsupportedMediaTypeException(String message, boolean raw) {
        super(message);
    }
}

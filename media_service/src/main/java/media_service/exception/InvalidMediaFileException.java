package media_service.exception;

public class InvalidMediaFileException extends RuntimeException {
    public InvalidMediaFileException(String message) {
        super(message);
    }
}

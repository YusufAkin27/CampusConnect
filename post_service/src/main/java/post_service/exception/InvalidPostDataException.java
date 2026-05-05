package post_service.exception;

public class InvalidPostDataException extends RuntimeException {
    public InvalidPostDataException(String message) {
        super(message);
    }
}

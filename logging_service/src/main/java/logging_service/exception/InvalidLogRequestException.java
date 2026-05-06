package logging_service.exception;

public class InvalidLogRequestException extends RuntimeException {
    public InvalidLogRequestException(String message) {
        super(message);
    }
}

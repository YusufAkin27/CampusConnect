package logging_service.exception;

public class LogNotFoundException extends RuntimeException {
    public LogNotFoundException(String message) {
        super(message);
    }

    public LogNotFoundException(Long id) {
        super("Log entry not found with id: " + id);
    }
}

package logging_service.exception;

public class ErrorLogNotFoundException extends RuntimeException {
    public ErrorLogNotFoundException(Long id) {
        super("Error log not found with id: " + id);
    }
}

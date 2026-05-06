package logging_service.exception;

public class RetentionPolicyNotFoundException extends RuntimeException {
    public RetentionPolicyNotFoundException(String message) {
        super(message);
    }
}

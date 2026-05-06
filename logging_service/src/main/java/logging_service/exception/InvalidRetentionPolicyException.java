package logging_service.exception;

public class InvalidRetentionPolicyException extends RuntimeException {
    public InvalidRetentionPolicyException(String message) {
        super(message);
    }
}

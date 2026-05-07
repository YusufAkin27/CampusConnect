package admin_service.exception;

public class InvalidReportStatusException extends RuntimeException {
    public InvalidReportStatusException(String message) {
        super(message);
    }
}

package admin_service.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}

package post_service.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(Long reportId) {
        super("Report not found with id: " + reportId);
    }
}

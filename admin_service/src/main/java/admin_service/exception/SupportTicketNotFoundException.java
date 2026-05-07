package admin_service.exception;

public class SupportTicketNotFoundException extends RuntimeException {
    public SupportTicketNotFoundException(String message) {
        super(message);
    }
}

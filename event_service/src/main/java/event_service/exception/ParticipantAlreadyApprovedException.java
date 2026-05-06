package event_service.exception;

public class ParticipantAlreadyApprovedException extends RuntimeException {
    public ParticipantAlreadyApprovedException(String message) {
        super(message);
    }
}

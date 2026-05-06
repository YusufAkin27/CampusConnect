package event_service.exception;

public class ParticipantAlreadyRejectedException extends RuntimeException {
    public ParticipantAlreadyRejectedException(String message) {
        super(message);
    }
}

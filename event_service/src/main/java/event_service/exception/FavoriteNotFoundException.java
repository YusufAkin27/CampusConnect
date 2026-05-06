package event_service.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String message) {
        super(message);
    }
}

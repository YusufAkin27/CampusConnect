package like_service.exception;

/**
 * Like kaydı bulunamadığında fırlatılır.
 */
public class LikeNotFoundException extends RuntimeException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}

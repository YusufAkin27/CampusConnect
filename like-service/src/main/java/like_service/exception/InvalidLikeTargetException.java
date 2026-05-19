package like_service.exception;

/**
 * Beğenilecek hedef bulunamadığında veya geçersiz olduğunda fırlatılır.
 */
public class InvalidLikeTargetException extends RuntimeException {

    public InvalidLikeTargetException(String message) {
        super(message);
    }
}

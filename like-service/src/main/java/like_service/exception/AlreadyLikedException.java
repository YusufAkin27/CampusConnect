package like_service.exception;

/**
 * İçerik zaten beğenilmiş olduğunda fırlatılır.
 * Not: Normal akışta idempotent davranılması tercih edilir.
 */
public class AlreadyLikedException extends RuntimeException {

    public AlreadyLikedException(String message) {
        super(message);
    }
}

package post_service.exception;

public class HashtagNotFoundException extends RuntimeException {
    public HashtagNotFoundException(String hashtag) {
        super("Hashtag not found: " + hashtag);
    }
}

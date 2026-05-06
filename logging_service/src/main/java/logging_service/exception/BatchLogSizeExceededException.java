package logging_service.exception;

public class BatchLogSizeExceededException extends RuntimeException {
    public BatchLogSizeExceededException(int received, int maxAllowed) {
        super("Batch log size exceeded. Received: " + received + ", max allowed: " + maxAllowed);
    }
}

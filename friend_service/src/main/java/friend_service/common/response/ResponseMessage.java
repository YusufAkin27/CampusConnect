package friend_service.common.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard response wrapper with no payload - used for operations
 * that only indicate success or failure (e.g., delete, cancel).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {

    private boolean success;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ResponseMessage of(boolean success, String message) {
        return ResponseMessage.builder()
                .success(success)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ResponseMessage success(String message) {
        return of(true, message);
    }

    public static ResponseMessage failure(String message) {
        return of(false, message);
    }
}

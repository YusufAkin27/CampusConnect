package user_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard response wrapper for operations that return no data payload.
 * Used for operations like deactivate, delete, status updates.
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

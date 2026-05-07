package admin_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simple response wrapper for operations that do not return a data payload.
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

    public static ResponseMessage success(String message) {
        return ResponseMessage.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ResponseMessage failure(String message) {
        return ResponseMessage.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

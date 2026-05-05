package friend_service.common.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard response wrapper that includes a generic data payload.
 * All data-returning endpoints should use this wrapper.
 *
 * @param <T> the type of the data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataResponseMessage<T> {

    private boolean success;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> DataResponseMessage<T> of(boolean success, String message, T data) {
        return DataResponseMessage.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> DataResponseMessage<T> success(String message, T data) {
        return of(true, message, data);
    }

    public static <T> DataResponseMessage<T> failure(String message, T data) {
        return of(false, message, data);
    }
}

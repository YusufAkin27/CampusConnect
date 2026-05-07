package admin_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic response wrapper for operations that return a data payload.
 *
 * @param <T> Type of the data payload
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

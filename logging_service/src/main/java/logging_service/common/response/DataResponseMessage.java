package logging_service.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static <T> DataResponseMessage<T> failure(String message) {
        return of(false, message, null);
    }
}

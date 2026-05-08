package story_service.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int status;
    private LocalDateTime timestamp;
    private String path;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .status(200)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .status(status)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> failure(String message, int status, String path) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(null)
            .status(status)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }

    public static <T> ApiResponse<T> failure(String message, T data) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
}

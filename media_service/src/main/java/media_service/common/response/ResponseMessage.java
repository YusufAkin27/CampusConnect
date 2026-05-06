package media_service.common.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public static ResponseMessage success(String message) {
        return ResponseMessage.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ResponseMessage error(String message) {
        return ResponseMessage.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

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

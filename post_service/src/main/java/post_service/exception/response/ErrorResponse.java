package post_service.exception.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String message;
    private String errorCode;
    private int status;
    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

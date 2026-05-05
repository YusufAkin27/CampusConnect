package post_service.exception.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

    private boolean success;
    private String message;
    private Map<String, String> validationErrors;
    private int status;
    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

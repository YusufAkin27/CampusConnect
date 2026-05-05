package post_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {

    @NotBlank(message = "Comment content must not be blank")
    @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
    private String content;
}

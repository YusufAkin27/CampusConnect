package post_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import post_service.enums.PostStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostStatusRequest {

    @NotNull(message = "Post status must not be null")
    private PostStatus status;
}

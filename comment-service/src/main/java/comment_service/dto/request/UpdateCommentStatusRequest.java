package comment_service.dto.request;

import comment_service.entity.CommentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentStatusRequest {

    @NotNull(message = "Yorum durumu boş olamaz")
    private CommentStatus status;
}

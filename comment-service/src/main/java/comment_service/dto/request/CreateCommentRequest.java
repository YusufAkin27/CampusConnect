package comment_service.dto.request;

import comment_service.entity.CommentTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequest {

    @NotNull(message = "targetId boş olamaz")
    private UUID targetId;

    @NotNull(message = "targetType boş olamaz")
    private CommentTargetType targetType;

    @NotBlank(message = "Yorum içeriği boş olamaz")
    @Size(min = 1, max = 1000, message = "Yorum 1-1000 karakter arasında olmalıdır")
    private String content;
}

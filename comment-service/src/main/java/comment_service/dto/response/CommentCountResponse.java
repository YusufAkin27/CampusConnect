package comment_service.dto.response;

import comment_service.entity.CommentTargetType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCountResponse {

    private UUID targetId;
    private CommentTargetType targetType;
    private long commentCount;
}

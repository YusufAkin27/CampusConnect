package comment_service.dto.response;

import comment_service.entity.CommentStatus;
import comment_service.entity.CommentTargetType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private UUID id;
    private UUID targetId;
    private CommentTargetType targetType;
    private UUID userId;
    private UUID parentCommentId;
    private String content;
    private CommentStatus status;
    private Long likeCount;
    private Long replyCount;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional user summary fields (populated via user-service when available)
    private String username;
    private String fullName;
    private String profilePhotoUrl;
}

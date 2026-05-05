package post_service.dto.response;

import lombok.*;
import post_service.enums.CommentStatus;
import post_service.enums.ReactionType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long authUserId;
    private UserSummaryResponse author;
    private Long parentCommentId;
    private String content;
    private CommentStatus status;
    private Long likeCount;
    private Long replyCount;
    private Long reportCount;

    // Current user interaction flags
    private Boolean likedByMe;
    private ReactionType myReaction;

    private Boolean edited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested replies (populated on demand)
    private List<CommentResponse> replies;
}

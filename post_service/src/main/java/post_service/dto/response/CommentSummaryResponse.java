package post_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentSummaryResponse {

    private Long id;
    private Long postId;
    private Long authUserId;
    private String content;
    private Long likeCount;
    private Long replyCount;
    private LocalDateTime createdAt;
}

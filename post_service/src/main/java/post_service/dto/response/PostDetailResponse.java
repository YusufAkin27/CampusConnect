package post_service.dto.response;

import lombok.*;
import post_service.enums.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private Long authUserId;
    private UserSummaryResponse author;
    private String content;
    private PostType postType;
    private PostVisibility visibility;
    private PostStatus status;
    private Boolean pinned;
    private Boolean commentsEnabled;
    private Boolean likesEnabled;
    private Long likeCount;
    private Long commentCount;
    private Long saveCount;
    private Long viewCount;
    private Long shareCount;
    private Long reportCount;

    // Current user interaction flags
    private Boolean likedByMe;
    private ReactionType myReaction;
    private Boolean savedByMe;

    private List<PostMediaResponse> mediaList;
    private List<String> hashtags;
    private List<MentionResponse> mentions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Extended detail
    private List<CommentResponse> latestComments;
}

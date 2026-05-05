package post_service.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostStatsResponse {

    private Long postId;
    private Long likeCount;
    private Long commentCount;
    private Long saveCount;
    private Long viewCount;
    private Long shareCount;
    private Long reportCount;
}

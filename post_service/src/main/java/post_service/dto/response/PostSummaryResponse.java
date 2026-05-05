package post_service.dto.response;

import lombok.*;
import post_service.enums.MediaType;
import post_service.enums.PostStatus;
import post_service.enums.PostVisibility;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {

    private Long id;
    private Long authUserId;
    private String contentPreview;
    private String firstMediaUrl;
    private MediaType firstMediaType;
    private Long likeCount;
    private Long commentCount;
    private Long saveCount;
    private PostVisibility visibility;
    private PostStatus status;
    private LocalDateTime createdAt;
}

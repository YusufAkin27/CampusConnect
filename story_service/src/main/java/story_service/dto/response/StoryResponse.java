package story_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import story_service.enums.MediaType;
import story_service.enums.StoryStatus;
import story_service.enums.StoryVisibility;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponse {

    private UUID id;
    private Long ownerUserId;
    private String ownerUsername;
    private Long mediaId;
    private String mediaUrl;
    private MediaType mediaType;
    private String caption;
    private StoryVisibility visibility;
    private StoryStatus status;
    private Long viewCount;
    private boolean viewed;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

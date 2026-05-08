package story_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryViewResponse {

    private UUID id;
    private UUID storyId;
    private Long viewerUserId;
    private String viewerUsername;
    private LocalDateTime viewedAt;
}

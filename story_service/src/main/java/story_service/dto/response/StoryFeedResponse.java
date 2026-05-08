package story_service.dto.response;

import java.util.List;
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
public class StoryFeedResponse {

    private Long userId;
    private String username;
    private String profileImageUrl;
    private boolean hasUnviewedStories;
    private List<StorySummaryResponse> stories;
}

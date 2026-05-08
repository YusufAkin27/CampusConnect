package story_service.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import story_service.dto.response.PageResponse;
import story_service.dto.response.StoryViewResponse;
import story_service.security.CurrentUser;

public interface StoryViewService {

    /**
     * Marks a story as viewed by the current user.
     * Returns the view response. If already viewed, returns existing view.
     * Story owner viewing their own story does not count as a view.
     */
    StoryViewResponse markAsViewed(UUID storyId, CurrentUser currentUser);

    /**
     * Gets the list of viewers for a story.
     * Only story owner or admin can access this.
     */
    PageResponse<StoryViewResponse> getStoryViews(UUID storyId, CurrentUser currentUser, Pageable pageable);

    /**
     * Gets the view count for a story.
     */
    long getViewCount(UUID storyId);
}

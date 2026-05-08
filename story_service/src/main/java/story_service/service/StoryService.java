package story_service.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import story_service.dto.request.CreateStoryRequest;
import story_service.dto.request.UpdateStoryRequest;
import story_service.dto.response.PageResponse;
import story_service.dto.response.StoryFeedResponse;
import story_service.dto.response.StoryResponse;
import story_service.dto.response.StorySummaryResponse;
import story_service.enums.StoryStatus;
import story_service.security.CurrentUser;

public interface StoryService {

    /**
     * Creates a new story with media validation.
     */
    StoryResponse createStory(CreateStoryRequest request, CurrentUser currentUser);

    /**
     * Gets the current user's active stories.
     */
    List<StorySummaryResponse> getMyStories(CurrentUser currentUser);

    /**
     * Gets the current user's story archive (all non-deleted, including expired).
     */
    PageResponse<StorySummaryResponse> getMyArchive(CurrentUser currentUser, Pageable pageable);

    /**
     * Gets a specific user's active stories, respecting visibility rules.
     */
    List<StorySummaryResponse> getUserStories(Long userId, CurrentUser currentUser);

    /**
     * Gets the story feed (active stories from followed users).
     */
    List<StoryFeedResponse> getFeedStories(CurrentUser currentUser);

    /**
     * Gets detailed story information by ID.
     */
    StoryResponse getStoryDetail(UUID storyId, CurrentUser currentUser);

    /**
     * Updates story caption and/or visibility.
     */
    StoryResponse updateStory(UUID storyId, UpdateStoryRequest request, CurrentUser currentUser);

    /**
     * Soft deletes a story (user can only delete own story).
     */
    void deleteStory(UUID storyId, CurrentUser currentUser);

    /**
     * Admin soft deletes any story.
     */
    void adminDeleteStory(UUID storyId, CurrentUser currentUser);

    /**
     * Admin lists all stories with filters.
     */
    PageResponse<StoryResponse> adminGetAllStories(StoryStatus status, Long userId,
                                                     String startDate, String endDate,
                                                     Pageable pageable);

    /**
     * Expires stories whose expiresAt has passed. Called by scheduler.
     */
    int expireOldStories();
}

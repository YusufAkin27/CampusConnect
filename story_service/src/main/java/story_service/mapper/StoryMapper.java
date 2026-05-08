package story_service.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import story_service.dto.request.CreateStoryRequest;
import story_service.dto.response.MediaValidationResponse;
import story_service.dto.response.PageResponse;
import story_service.dto.response.StoryFeedResponse;
import story_service.dto.response.StoryResponse;
import story_service.dto.response.StorySummaryResponse;
import story_service.dto.response.StoryViewResponse;
import story_service.entity.Story;
import story_service.entity.StoryView;
import story_service.enums.MediaType;
import story_service.enums.StoryStatus;
import story_service.enums.StoryVisibility;
import story_service.security.CurrentUser;

@Component
public class StoryMapper {

    /**
     * Maps CreateStoryRequest + media info to Story entity.
     */
    public Story toEntity(CreateStoryRequest request, CurrentUser currentUser,
                          MediaValidationResponse mediaInfo) {
        StoryVisibility visibility = request.getVisibility() != null
            ? request.getVisibility()
            : StoryVisibility.FOLLOWERS_ONLY;

        MediaType mediaType = null;
        if (mediaInfo.getMediaType() != null) {
            try {
                mediaType = MediaType.valueOf(mediaInfo.getMediaType().toUpperCase());
            } catch (IllegalArgumentException e) {
                mediaType = MediaType.IMAGE; // default fallback
            }
        }

        return Story.builder()
            .ownerUserId(currentUser.getUserId())
            .ownerUsername(currentUser.getUsername())
            .mediaId(request.getMediaId())
            .mediaUrl(mediaInfo.getMediaUrl())
            .mediaType(mediaType)
            .caption(request.getCaption())
            .visibility(visibility)
            .status(StoryStatus.ACTIVE)
            .viewCount(0L)
            .build();
    }

    /**
     * Maps Story entity to full StoryResponse.
     */
    public StoryResponse toResponse(Story story, boolean viewed) {
        return StoryResponse.builder()
            .id(story.getId())
            .ownerUserId(story.getOwnerUserId())
            .ownerUsername(story.getOwnerUsername())
            .mediaId(story.getMediaId())
            .mediaUrl(story.getMediaUrl())
            .mediaType(story.getMediaType())
            .caption(story.getCaption())
            .visibility(story.getVisibility())
            .status(story.getStatus())
            .viewCount(story.getViewCount())
            .viewed(viewed)
            .createdAt(story.getCreatedAt())
            .expiresAt(story.getExpiresAt())
            .build();
    }

    /**
     * Maps Story entity to summary response.
     */
    public StorySummaryResponse toSummaryResponse(Story story, boolean viewed) {
        return StorySummaryResponse.builder()
            .id(story.getId())
            .ownerUserId(story.getOwnerUserId())
            .ownerUsername(story.getOwnerUsername())
            .mediaUrl(story.getMediaUrl())
            .mediaType(story.getMediaType())
            .caption(story.getCaption())
            .viewed(viewed)
            .createdAt(story.getCreatedAt())
            .expiresAt(story.getExpiresAt())
            .build();
    }

    /**
     * Maps a list of stories to summary responses with batch viewed check.
     */
    public List<StorySummaryResponse> toSummaryResponses(List<Story> stories, Set<UUID> viewedStoryIds) {
        if (stories == null || stories.isEmpty()) {
            return Collections.emptyList();
        }
        Set<UUID> viewed = viewedStoryIds != null ? viewedStoryIds : Collections.emptySet();
        return stories.stream()
            .map(story -> toSummaryResponse(story, viewed.contains(story.getId())))
            .collect(Collectors.toList());
    }

    /**
     * Maps StoryView entity to response.
     */
    public StoryViewResponse toViewResponse(StoryView view) {
        return StoryViewResponse.builder()
            .id(view.getId())
            .storyId(view.getStoryId())
            .viewerUserId(view.getViewerUserId())
            .viewerUsername(view.getViewerUsername())
            .viewedAt(view.getViewedAt())
            .build();
    }

    /**
     * Maps a list of StoryView entities to responses.
     */
    public List<StoryViewResponse> toViewResponses(List<StoryView> views) {
        if (views == null || views.isEmpty()) {
            return Collections.emptyList();
        }
        return views.stream()
            .map(this::toViewResponse)
            .collect(Collectors.toList());
    }

    /**
     * Maps a list of stories grouped by user to StoryFeedResponse.
     */
    public StoryFeedResponse toFeedResponse(Long userId, String username, String profileImageUrl,
                                             List<Story> stories, Set<UUID> viewedStoryIds) {
        List<StorySummaryResponse> storySummaries = toSummaryResponses(stories, viewedStoryIds);
        boolean hasUnviewed = storySummaries.stream().anyMatch(s -> !s.isViewed());

        return StoryFeedResponse.builder()
            .userId(userId)
            .username(username)
            .profileImageUrl(profileImageUrl)
            .hasUnviewedStories(hasUnviewed)
            .stories(storySummaries)
            .build();
    }

    /**
     * Creates a PageResponse from a Spring Data Page.
     */
    public <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
        return PageResponse.<T>builder()
            .content(content)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}

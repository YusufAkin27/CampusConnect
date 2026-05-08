package story_service.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story_service.client.FriendServiceClient;
import story_service.client.MediaServiceClient;
import story_service.client.UserServiceClient;
import story_service.dto.request.CreateStoryRequest;
import story_service.dto.request.UpdateStoryRequest;
import story_service.dto.response.MediaValidationResponse;
import story_service.dto.response.PageResponse;
import story_service.dto.response.StoryFeedResponse;
import story_service.dto.response.StoryResponse;
import story_service.dto.response.StorySummaryResponse;
import story_service.dto.response.UserSummaryResponse;
import story_service.entity.Story;
import story_service.enums.StoryStatus;
import story_service.enums.StoryVisibility;
import story_service.exception.InvalidStoryMediaException;
import story_service.exception.MediaServiceUnavailableException;
import story_service.exception.StoryAccessDeniedException;
import story_service.exception.StoryAlreadyDeletedException;
import story_service.exception.StoryExpiredException;
import story_service.exception.StoryNotFoundException;
import story_service.mapper.StoryMapper;
import story_service.repository.StoryRepository;
import story_service.repository.StoryViewRepository;
import story_service.security.CurrentUser;
import story_service.service.StoryService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final StoryViewRepository storyViewRepository;
    private final MediaServiceClient mediaServiceClient;
    private final UserServiceClient userServiceClient;
    private final FriendServiceClient friendServiceClient;
    private final StoryMapper storyMapper;

    private static final List<String> ALLOWED_MEDIA_TYPES = Arrays.asList("IMAGE", "VIDEO");

    // ==================== Story Creation ====================

    @Override
    public StoryResponse createStory(CreateStoryRequest request, CurrentUser currentUser) {
        log.info("Creating story for user: {} (userId={})", currentUser.getUsername(), currentUser.getUserId());

        // 1. Validate media via media-service
        MediaValidationResponse mediaInfo = validateMedia(request.getMediaId(), currentUser.getUserId());

        // 2. Create story entity
        Story story = storyMapper.toEntity(request, currentUser, mediaInfo);
        story = storyRepository.save(story);

        log.info("Story created successfully. storyId={}, userId={}", story.getId(), currentUser.getUserId());

        // 3. Register media usage (fire-and-forget, non-critical)
        try {
            mediaServiceClient.registerMediaUsage(
                request.getMediaId(),
                "STORY",
                story.getId().toString()
            );
            log.debug("Media usage registered for storyId={}, mediaId={}", story.getId(), request.getMediaId());
        } catch (Exception e) {
            log.warn("Failed to register media usage for storyId={}, mediaId={}. Error: {}",
                story.getId(), request.getMediaId(), e.getMessage());
            // Non-critical - story is still created
        }

        return storyMapper.toResponse(story, false);
    }

    // ==================== Story Retrieval ====================

    @Override
    @Transactional(readOnly = true)
    public List<StorySummaryResponse> getMyStories(CurrentUser currentUser) {
        log.debug("Getting active stories for user: {}", currentUser.getUserId());

        List<Story> stories = storyRepository.findActiveStoriesByOwner(
            currentUser.getUserId(), LocalDateTime.now()
        );

        Set<UUID> viewedStoryIds = getViewedStoryIds(stories, currentUser.getUserId());
        return storyMapper.toSummaryResponses(stories, viewedStoryIds);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StorySummaryResponse> getMyArchive(CurrentUser currentUser, Pageable pageable) {
        log.debug("Getting story archive for user: {}", currentUser.getUserId());

        Page<Story> storyPage = storyRepository.findAllStoriesByOwner(currentUser.getUserId(), pageable);
        Set<UUID> viewedStoryIds = getViewedStoryIds(storyPage.getContent(), currentUser.getUserId());
        List<StorySummaryResponse> summaries = storyMapper.toSummaryResponses(storyPage.getContent(), viewedStoryIds);

        return storyMapper.toPageResponse(storyPage, summaries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorySummaryResponse> getUserStories(Long userId, CurrentUser currentUser) {
        log.debug("Getting stories for user {} requested by user {}", userId, currentUser.getUserId());

        // Determine which visibilities the current user can see
        List<StoryVisibility> allowedVisibilities = resolveAllowedVisibilities(userId, currentUser);

        if (allowedVisibilities.isEmpty()) {
            return Collections.emptyList();
        }

        List<Story> stories = storyRepository.findActiveStoriesByOwnerAndVisibility(
            userId, LocalDateTime.now(), allowedVisibilities
        );

        Set<UUID> viewedStoryIds = getViewedStoryIds(stories, currentUser.getUserId());
        return storyMapper.toSummaryResponses(stories, viewedStoryIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoryFeedResponse> getFeedStories(CurrentUser currentUser) {
        log.debug("Getting story feed for user: {}", currentUser.getUserId());

        // 1. Get following list from friend-service
        List<Long> followingUserIds;
        try {
            followingUserIds = friendServiceClient.getFollowingUserIds(currentUser.getUserId());
        } catch (Exception e) {
            log.warn("Failed to get following list for user {}. Returning empty feed. Error: {}",
                currentUser.getUserId(), e.getMessage());
            return Collections.emptyList();
        }

        if (followingUserIds == null || followingUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Get active stories from followed users (PUBLIC + FOLLOWERS_ONLY)
        List<StoryVisibility> visibilities = Arrays.asList(
            StoryVisibility.PUBLIC, StoryVisibility.FOLLOWERS_ONLY
        );
        List<Story> allStories = storyRepository.findActiveStoriesByOwnerIds(
            followingUserIds, LocalDateTime.now(), visibilities
        );

        if (allStories.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Get viewed story IDs for the current user
        Set<UUID> viewedStoryIds = getViewedStoryIds(allStories, currentUser.getUserId());

        // 4. Group stories by user
        Map<Long, List<Story>> storiesByUser = allStories.stream()
            .collect(Collectors.groupingBy(Story::getOwnerUserId, LinkedHashMap::new, Collectors.toList()));

        // 5. Build feed responses
        List<StoryFeedResponse> feedResponses = new ArrayList<>();
        for (Map.Entry<Long, List<Story>> entry : storiesByUser.entrySet()) {
            Long userId = entry.getKey();
            List<Story> userStories = entry.getValue();

            // Get user profile info
            String username = userStories.get(0).getOwnerUsername();
            String profileImageUrl = null;
            try {
                UserSummaryResponse userSummary = userServiceClient.getUserSummary(userId);
                if (userSummary != null) {
                    username = userSummary.getUsername() != null ? userSummary.getUsername() : username;
                    profileImageUrl = userSummary.getProfileImageUrl();
                }
            } catch (Exception e) {
                log.debug("Could not fetch user summary for userId={}. Using stored username.", userId);
            }

            StoryFeedResponse feedResponse = storyMapper.toFeedResponse(
                userId, username, profileImageUrl, userStories, viewedStoryIds
            );
            feedResponses.add(feedResponse);
        }

        // 6. Sort: users with unviewed stories first
        feedResponses.sort((a, b) -> {
            if (a.isHasUnviewedStories() && !b.isHasUnviewedStories()) return -1;
            if (!a.isHasUnviewedStories() && b.isHasUnviewedStories()) return 1;
            return 0;
        });

        return feedResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public StoryResponse getStoryDetail(UUID storyId, CurrentUser currentUser) {
        log.debug("Getting story detail for storyId={} by user={}", storyId, currentUser.getUserId());

        Story story = findStoryOrThrow(storyId);

        // Check if story is deleted
        if (story.isDeleted()) {
            throw new StoryNotFoundException("Story not found with id: " + storyId);
        }

        // Check visibility access
        if (!canUserViewStory(story, currentUser)) {
            throw new StoryAccessDeniedException("You do not have permission to view this story");
        }

        // Check if story is expired (owner can still see their expired stories)
        if (story.isExpired() && !story.isOwnedBy(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new StoryExpiredException("This story has expired");
        }

        boolean viewed = storyViewRepository.existsByStoryIdAndViewerUserId(storyId, currentUser.getUserId());
        return storyMapper.toResponse(story, viewed);
    }

    // ==================== Story Update ====================

    @Override
    public StoryResponse updateStory(UUID storyId, UpdateStoryRequest request, CurrentUser currentUser) {
        log.info("Updating story storyId={} by user={}", storyId, currentUser.getUserId());

        Story story = findStoryOrThrow(storyId);

        // Only owner or admin can update
        if (!story.isOwnedBy(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new StoryAccessDeniedException("You can only update your own stories");
        }

        if (story.isDeleted()) {
            throw new StoryAlreadyDeletedException("Cannot update a deleted story");
        }

        // Update fields if provided
        if (request.getCaption() != null) {
            story.setCaption(request.getCaption());
        }
        if (request.getVisibility() != null) {
            story.setVisibility(request.getVisibility());
        }

        story = storyRepository.save(story);
        log.info("Story updated successfully. storyId={}", storyId);

        boolean viewed = storyViewRepository.existsByStoryIdAndViewerUserId(storyId, currentUser.getUserId());
        return storyMapper.toResponse(story, viewed);
    }

    // ==================== Story Deletion ====================

    @Override
    public void deleteStory(UUID storyId, CurrentUser currentUser) {
        log.info("Deleting story storyId={} by user={}", storyId, currentUser.getUserId());

        Story story = findStoryOrThrow(storyId);

        // Only owner can delete (or use adminDeleteStory for admin)
        if (!story.isOwnedBy(currentUser.getUserId())) {
            throw new StoryAccessDeniedException("You can only delete your own stories");
        }

        if (story.isDeleted()) {
            throw new StoryAlreadyDeletedException("Story is already deleted");
        }

        performSoftDelete(story, currentUser.getUsername());
        log.info("Story soft-deleted successfully. storyId={}, deletedBy={}", storyId, currentUser.getUsername());
    }

    @Override
    public void adminDeleteStory(UUID storyId, CurrentUser currentUser) {
        log.info("Admin deleting story storyId={} by admin={}", storyId, currentUser.getUsername());

        if (!currentUser.isAdmin()) {
            throw new StoryAccessDeniedException("Only admins can use this endpoint");
        }

        Story story = findStoryOrThrow(storyId);

        if (story.isDeleted()) {
            throw new StoryAlreadyDeletedException("Story is already deleted");
        }

        performSoftDelete(story, "ADMIN:" + currentUser.getUsername());
        log.info("Story admin-deleted successfully. storyId={}, deletedBy=ADMIN:{}", storyId, currentUser.getUsername());
    }

    // ==================== Admin Operations ====================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoryResponse> adminGetAllStories(StoryStatus status, Long userId,
                                                           String startDate, String endDate,
                                                           Pageable pageable) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (startDate != null && !startDate.isBlank()) {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }
        if (endDate != null && !endDate.isBlank()) {
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        }

        Page<Story> storyPage = storyRepository.findAllWithFilters(status, userId, start, end, pageable);

        List<StoryResponse> responses = storyPage.getContent().stream()
            .map(story -> storyMapper.toResponse(story, false))
            .collect(Collectors.toList());

        return storyMapper.toPageResponse(storyPage, responses);
    }

    // ==================== Expiration ====================

    @Override
    public int expireOldStories() {
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = storyRepository.bulkExpireStories(now);
        if (expiredCount > 0) {
            log.info("Expired {} stories at {}", expiredCount, now);
        }
        return expiredCount;
    }

    // ==================== Private Helper Methods ====================

    private MediaValidationResponse validateMedia(Long mediaId, Long userId) {
        MediaValidationResponse mediaInfo;
        try {
            mediaInfo = mediaServiceClient.validateMedia(mediaId, userId);
        } catch (Exception e) {
            log.error("Failed to validate media with media-service. mediaId={}, error={}", mediaId, e.getMessage());
            throw new MediaServiceUnavailableException("Media service is currently unavailable. Please try again later.", e);
        }

        if (mediaInfo == null || !mediaInfo.isValid()) {
            String message = mediaInfo != null && mediaInfo.getMessage() != null
                ? mediaInfo.getMessage()
                : "Media validation failed for mediaId: " + mediaId;
            throw new InvalidStoryMediaException(message);
        }

        // Check media owner
        if (mediaInfo.getOwnerUserId() != null && !mediaInfo.getOwnerUserId().equals(userId)) {
            throw new InvalidStoryMediaException("You can only use your own media for stories");
        }

        // Check media type
        if (mediaInfo.getMediaType() != null && !ALLOWED_MEDIA_TYPES.contains(mediaInfo.getMediaType().toUpperCase())) {
            throw new InvalidStoryMediaException("Media type '" + mediaInfo.getMediaType() +
                "' is not supported for stories. Allowed types: " + ALLOWED_MEDIA_TYPES);
        }

        return mediaInfo;
    }

    private Story findStoryOrThrow(UUID storyId) {
        return storyRepository.findById(storyId)
            .orElseThrow(() -> new StoryNotFoundException("Story not found with id: " + storyId));
    }

    private void performSoftDelete(Story story, String deletedBy) {
        story.setDeletedAt(LocalDateTime.now());
        story.setDeletedBy(deletedBy);
        story.setStatus(StoryStatus.DELETED);
        storyRepository.save(story);
    }

    private boolean canUserViewStory(Story story, CurrentUser currentUser) {
        // Owner can always see their own stories
        if (story.isOwnedBy(currentUser.getUserId())) {
            return true;
        }

        // Admin can see everything
        if (currentUser.isAdmin()) {
            return true;
        }

        switch (story.getVisibility()) {
            case PUBLIC:
                return true;
            case FOLLOWERS_ONLY:
                try {
                    return friendServiceClient.isFollowing(currentUser.getUserId(), story.getOwnerUserId());
                } catch (Exception e) {
                    log.warn("Friend service check failed for visibility. Denying access. Error: {}", e.getMessage());
                    return false;
                }
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }

    private List<StoryVisibility> resolveAllowedVisibilities(Long targetUserId, CurrentUser currentUser) {
        // Owner can see all their own stories
        if (targetUserId.equals(currentUser.getUserId())) {
            return Arrays.asList(StoryVisibility.PUBLIC, StoryVisibility.FOLLOWERS_ONLY, StoryVisibility.PRIVATE);
        }

        // Admin can see all
        if (currentUser.isAdmin()) {
            return Arrays.asList(StoryVisibility.PUBLIC, StoryVisibility.FOLLOWERS_ONLY, StoryVisibility.PRIVATE);
        }

        List<StoryVisibility> visibilities = new ArrayList<>();
        visibilities.add(StoryVisibility.PUBLIC);

        // Check if current user follows the target user
        try {
            boolean isFollowing = friendServiceClient.isFollowing(currentUser.getUserId(), targetUserId);
            if (isFollowing) {
                visibilities.add(StoryVisibility.FOLLOWERS_ONLY);
            }
        } catch (Exception e) {
            log.warn("Friend service check failed. Only showing PUBLIC stories. Error: {}", e.getMessage());
        }

        return visibilities;
    }

    private Set<UUID> getViewedStoryIds(List<Story> stories, Long viewerUserId) {
        if (stories == null || stories.isEmpty() || viewerUserId == null) {
            return Collections.emptySet();
        }

        List<UUID> storyIds = stories.stream()
            .map(Story::getId)
            .collect(Collectors.toList());

        List<UUID> viewedIds = storyViewRepository.findViewedStoryIds(viewerUserId, storyIds);
        return new HashSet<>(viewedIds);
    }
}

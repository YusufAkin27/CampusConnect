package story_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import story_service.entity.StoryView;

@Repository
public interface StoryViewRepository extends JpaRepository<StoryView, UUID> {

    /**
     * Check if a viewer has already viewed a specific story.
     */
    boolean existsByStoryIdAndViewerUserId(UUID storyId, Long viewerUserId);

    /**
     * Find a specific view record.
     */
    Optional<StoryView> findByStoryIdAndViewerUserId(UUID storyId, Long viewerUserId);

    /**
     * Find all view records for a specific story, ordered by viewedAt descending.
     */
    Page<StoryView> findByStoryIdOrderByViewedAtDesc(UUID storyId, Pageable pageable);

    /**
     * Find all view records for a specific story (no pagination).
     */
    List<StoryView> findByStoryIdOrderByViewedAtDesc(UUID storyId);

    /**
     * Count views for a specific story.
     */
    long countByStoryId(UUID storyId);

    /**
     * Find story IDs that a specific viewer has viewed from a list of story IDs.
     * Used for batch checking viewed status in feed.
     */
    @Query("SELECT sv.storyId FROM StoryView sv WHERE sv.viewerUserId = :viewerUserId " +
           "AND sv.storyId IN :storyIds")
    List<UUID> findViewedStoryIds(
        @Param("viewerUserId") Long viewerUserId,
        @Param("storyIds") List<UUID> storyIds
    );

    /**
     * Delete all view records for a specific story (for cleanup).
     */
    void deleteAllByStoryId(UUID storyId);
}

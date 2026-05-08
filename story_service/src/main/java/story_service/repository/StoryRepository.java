package story_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import story_service.entity.Story;
import story_service.enums.StoryStatus;
import story_service.enums.StoryVisibility;

@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {

    // ==================== Active Stories Queries ====================

    /**
     * Find active stories by owner (not deleted, not expired, ACTIVE status).
     */
    @Query("SELECT s FROM Story s WHERE s.ownerUserId = :ownerUserId " +
           "AND s.status = 'ACTIVE' " +
           "AND s.deletedAt IS NULL " +
           "AND s.expiresAt > :now " +
           "ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesByOwner(
        @Param("ownerUserId") Long ownerUserId,
        @Param("now") LocalDateTime now
    );

    /**
     * Find all stories by owner including expired ones (for archive).
     * Excludes deleted stories.
     */
    @Query("SELECT s FROM Story s WHERE s.ownerUserId = :ownerUserId " +
           "AND s.deletedAt IS NULL " +
           "ORDER BY s.createdAt DESC")
    Page<Story> findAllStoriesByOwner(
        @Param("ownerUserId") Long ownerUserId,
        Pageable pageable
    );

    /**
     * Find active stories by owner with specific visibility.
     */
    @Query("SELECT s FROM Story s WHERE s.ownerUserId = :ownerUserId " +
           "AND s.status = 'ACTIVE' " +
           "AND s.deletedAt IS NULL " +
           "AND s.expiresAt > :now " +
           "AND s.visibility IN :visibilities " +
           "ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesByOwnerAndVisibility(
        @Param("ownerUserId") Long ownerUserId,
        @Param("now") LocalDateTime now,
        @Param("visibilities") List<StoryVisibility> visibilities
    );

    /**
     * Find active stories by multiple owners (for feed).
     */
    @Query("SELECT s FROM Story s WHERE s.ownerUserId IN :ownerUserIds " +
           "AND s.status = 'ACTIVE' " +
           "AND s.deletedAt IS NULL " +
           "AND s.expiresAt > :now " +
           "AND s.visibility IN :visibilities " +
           "ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesByOwnerIds(
        @Param("ownerUserIds") List<Long> ownerUserIds,
        @Param("now") LocalDateTime now,
        @Param("visibilities") List<StoryVisibility> visibilities
    );

    // ==================== Expiration Queries ====================

    /**
     * Find stories that need to be expired.
     * Status is ACTIVE but expiresAt has passed.
     */
    @Query("SELECT s FROM Story s WHERE s.status = 'ACTIVE' " +
           "AND s.expiresAt <= :now " +
           "AND s.deletedAt IS NULL")
    List<Story> findExpiredActiveStories(@Param("now") LocalDateTime now);

    /**
     * Bulk expire active stories that have passed their expiration time.
     */
    @Modifying
    @Query("UPDATE Story s SET s.status = 'EXPIRED', s.updatedAt = :now " +
           "WHERE s.status = 'ACTIVE' AND s.expiresAt <= :now")
    int bulkExpireStories(@Param("now") LocalDateTime now);

    // ==================== Admin Queries ====================

    /**
     * Find all stories with optional filters for admin.
     */
    @Query("SELECT s FROM Story s WHERE " +
           "(:status IS NULL OR s.status = :status) " +
           "AND (:ownerUserId IS NULL OR s.ownerUserId = :ownerUserId) " +
           "AND (:startDate IS NULL OR s.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR s.createdAt <= :endDate) " +
           "ORDER BY s.createdAt DESC")
    Page<Story> findAllWithFilters(
        @Param("status") StoryStatus status,
        @Param("ownerUserId") Long ownerUserId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // ==================== Count Queries ====================

    /**
     * Count active stories by owner.
     */
    @Query("SELECT COUNT(s) FROM Story s WHERE s.ownerUserId = :ownerUserId " +
           "AND s.status = 'ACTIVE' " +
           "AND s.deletedAt IS NULL " +
           "AND s.expiresAt > :now")
    long countActiveStoriesByOwner(
        @Param("ownerUserId") Long ownerUserId,
        @Param("now") LocalDateTime now
    );

    /**
     * Check if owner has active stories (for feed grouping).
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Story s " +
           "WHERE s.ownerUserId = :ownerUserId " +
           "AND s.status = 'ACTIVE' " +
           "AND s.deletedAt IS NULL " +
           "AND s.expiresAt > :now")
    boolean hasActiveStories(
        @Param("ownerUserId") Long ownerUserId,
        @Param("now") LocalDateTime now
    );
}

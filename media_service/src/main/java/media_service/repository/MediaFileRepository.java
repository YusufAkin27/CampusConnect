package media_service.repository;

import media_service.entity.MediaFile;
import media_service.enums.MediaContext;
import media_service.enums.MediaStatus;
import media_service.enums.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    Optional<MediaFile> findByIdAndStatus(Long id, MediaStatus status);

    List<MediaFile> findByIdInAndStatus(List<Long> ids, MediaStatus status);

    Page<MediaFile> findByOwnerAuthUserIdAndStatus(
            Long ownerAuthUserId,
            MediaStatus status,
            Pageable pageable
    );

    Page<MediaFile> findByOwnerAuthUserIdAndMediaContextAndStatus(
            Long ownerAuthUserId,
            MediaContext mediaContext,
            MediaStatus status,
            Pageable pageable
    );

    Page<MediaFile> findByOwnerAuthUserIdAndMediaTypeAndStatus(
            Long ownerAuthUserId,
            MediaType mediaType,
            MediaStatus status,
            Pageable pageable
    );

    Page<MediaFile> findByOwnerAuthUserIdAndMediaContextAndMediaTypeAndStatus(
            Long ownerAuthUserId,
            MediaContext mediaContext,
            MediaType mediaType,
            MediaStatus status,
            Pageable pageable
    );

    boolean existsByStorageKey(String storageKey);

    Long countByMediaType(MediaType mediaType);

    Long countByStatus(MediaStatus status);

    @Query("SELECT COALESCE(SUM(mf.fileSize), 0) FROM MediaFile mf WHERE mf.status = :status")
    Long sumFileSizeByStatus(@Param("status") MediaStatus status);

    @Query("SELECT COALESCE(SUM(mf.fileSize), 0) FROM MediaFile mf")
    Long sumAllFileSize();

    @Query("SELECT mf FROM MediaFile mf WHERE mf.ownerAuthUserId = :ownerAuthUserId " +
           "AND mf.status = :status " +
           "AND (:mediaContext IS NULL OR mf.mediaContext = :mediaContext) " +
           "AND (:mediaType IS NULL OR mf.mediaType = :mediaType)")
    Page<MediaFile> searchMedia(
            @Param("ownerAuthUserId") Long ownerAuthUserId,
            @Param("mediaContext") MediaContext mediaContext,
            @Param("mediaType") MediaType mediaType,
            @Param("status") MediaStatus status,
            Pageable pageable
    );

    /**
     * Finds TEMPORARY media older than a given time that have no active usage records.
     * Used by the orphan cleanup scheduler.
     */
    @Query("SELECT mf FROM MediaFile mf WHERE mf.status = 'TEMPORARY' " +
           "AND mf.createdAt < :cutoffTime " +
           "AND mf.id NOT IN (SELECT mu.mediaFileId FROM MediaUsage mu WHERE mu.status = 'ACTIVE')")
    List<MediaFile> findOrphanTemporaryMedia(@Param("cutoffTime") LocalDateTime cutoffTime);
}

package media_service.repository;

import media_service.entity.MediaUsage;
import media_service.enums.MediaUsageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaUsageRepository extends JpaRepository<MediaUsage, Long> {

    boolean existsByMediaFileIdAndServiceNameAndTargetTypeAndTargetId(
            Long mediaFileId,
            String serviceName,
            String targetType,
            Long targetId
    );

    Optional<MediaUsage> findByMediaFileIdAndServiceNameAndTargetTypeAndTargetId(
            Long mediaFileId,
            String serviceName,
            String targetType,
            Long targetId
    );

    List<MediaUsage> findByMediaFileIdAndStatus(Long mediaFileId, MediaUsageStatus status);

    List<MediaUsage> findByServiceNameAndTargetTypeAndTargetIdAndStatus(
            String serviceName,
            String targetType,
            Long targetId,
            MediaUsageStatus status
    );

    Page<MediaUsage> findByAuthUserIdAndStatus(
            Long authUserId,
            MediaUsageStatus status,
            Pageable pageable
    );

    boolean existsByMediaFileIdAndStatus(Long mediaFileId, MediaUsageStatus status);
}

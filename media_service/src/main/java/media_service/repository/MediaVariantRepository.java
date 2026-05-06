package media_service.repository;

import media_service.entity.MediaVariant;
import media_service.enums.MediaVariantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaVariantRepository extends JpaRepository<MediaVariant, Long> {

    List<MediaVariant> findByMediaFileId(Long mediaFileId);

    Optional<MediaVariant> findByMediaFileIdAndVariantType(Long mediaFileId, MediaVariantType variantType);

    void deleteByMediaFileId(Long mediaFileId);
}

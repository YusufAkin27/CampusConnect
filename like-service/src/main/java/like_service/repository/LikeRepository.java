package like_service.repository;

import like_service.entity.Like;
import like_service.entity.LikeStatus;
import like_service.entity.LikeTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    /**
     * Kullanıcının belirli bir hedef için beğeni kaydını bul.
     */
    Optional<Like> findByUserIdAndTargetIdAndTargetType(UUID userId, UUID targetId, LikeTargetType targetType);

    /**
     * Kullanıcının belirli bir hedef için belirli durumda kaydı var mı kontrol et.
     */
    boolean existsByUserIdAndTargetIdAndTargetTypeAndStatus(UUID userId, UUID targetId, LikeTargetType targetType, LikeStatus status);

    /**
     * Belirli bir hedefin aktif beğeni sayısını getir.
     */
    long countByTargetIdAndTargetTypeAndStatus(UUID targetId, LikeTargetType targetType, LikeStatus status);

    /**
     * Kullanıcının tüm aktif beğenilerini getir.
     */
    Page<Like> findByUserIdAndStatus(UUID userId, LikeStatus status, Pageable pageable);

    /**
     * Kullanıcının belirli türdeki aktif beğenilerini getir.
     */
    Page<Like> findByUserIdAndTargetTypeAndStatus(UUID userId, LikeTargetType targetType, LikeStatus status, Pageable pageable);

    /**
     * Belirli bir hedefin tüm beğenilerini soft-remove yap.
     * İçerik silindiğinde kullanılır.
     */
    @Modifying
    @Query("UPDATE Like l SET l.status = 'REMOVED', l.updatedAt = CURRENT_TIMESTAMP WHERE l.targetId = :targetId AND l.targetType = :targetType AND l.status = 'ACTIVE'")
    int softRemoveByTargetIdAndTargetType(@Param("targetId") UUID targetId, @Param("targetType") LikeTargetType targetType);
}

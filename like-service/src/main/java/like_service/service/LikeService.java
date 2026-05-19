package like_service.service;

import like_service.dto.request.BulkLikeCountRequest;
import like_service.dto.request.LikeRequest;
import like_service.dto.response.*;
import like_service.entity.LikeTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Like servis arayüzü.
 * Tüm beğeni işlemlerini yönetir.
 */
public interface LikeService {

    /**
     * İçerik beğen.
     * Aynı kullanıcı aynı içeriği tekrar beğenirse idempotent davranır.
     */
    LikeStatusResponse like(LikeRequest request, UUID currentUserId);

    /**
     * İçerik beğenisini kaldır (soft remove).
     */
    LikeStatusResponse unlike(LikeRequest request, UUID currentUserId);

    /**
     * Toggle like: beğenmişse kaldır, beğenmemişse beğen.
     */
    LikeStatusResponse toggle(LikeRequest request, UUID currentUserId);

    /**
     * Belirli bir içeriğin aktif beğeni sayısını getir.
     */
    LikeCountResponse getLikeCount(UUID targetId, LikeTargetType targetType);

    /**
     * Mevcut kullanıcının bir içeriği beğenip beğenmediğini ve like sayısını getir.
     */
    LikeStatusResponse getLikeStatus(UUID targetId, LikeTargetType targetType, UUID currentUserId);

    /**
     * Mevcut kullanıcının beğendiği içerikleri listele.
     */
    Page<LikedTargetResponse> getMyLikes(UUID currentUserId, LikeTargetType targetType, Pageable pageable);

    /**
     * Belirli bir kullanıcının beğendiği içerikleri listele.
     */
    Page<LikedTargetResponse> getUserLikes(UUID userId, LikeTargetType targetType, Pageable pageable);

    /**
     * Toplu beğeni sayılarını getir.
     */
    BulkLikeCountResponse getBulkLikeCounts(BulkLikeCountRequest request);

    /**
     * Belirli bir hedefe ait tüm beğenileri pasifleştir (soft remove).
     * İçerik silindiğinde çağrılır.
     */
    void removeLikesByTarget(UUID targetId, LikeTargetType targetType);
}

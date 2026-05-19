package like_service.service.impl;

import like_service.client.*;
import like_service.dto.request.BulkLikeCountRequest;
import like_service.dto.request.LikeRequest;
import like_service.dto.response.*;
import like_service.entity.Like;
import like_service.entity.LikeStatus;
import like_service.entity.LikeTargetType;
import like_service.exception.InvalidLikeTargetException;
import like_service.mapper.LikeMapper;
import like_service.repository.LikeRepository;
import like_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostServiceClient postServiceClient;
    private final CommentServiceClient commentServiceClient;
    private final StoryServiceClient storyServiceClient;
    private final EventServiceClient eventServiceClient;
    private final MediaServiceClient mediaServiceClient;

    @Override
    public LikeStatusResponse like(LikeRequest request, UUID currentUserId) {
        log.debug("Like isteği: userId={}, targetId={}, targetType={}", currentUserId, request.getTargetId(), request.getTargetType());

        // Hedef var mı kontrol et
        validateTargetExists(request.getTargetId(), request.getTargetType());

        // Mevcut kayıt var mı kontrol et
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(
                currentUserId, request.getTargetId(), request.getTargetType());

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.getStatus() == LikeStatus.ACTIVE) {
                // Zaten beğenmiş, idempotent davran
                log.debug("İçerik zaten beğenilmiş. userId={}, targetId={}", currentUserId, request.getTargetId());
            } else {
                // REMOVED durumunda, tekrar aktif yap
                like.setStatus(LikeStatus.ACTIVE);
                like.setUpdatedAt(LocalDateTime.now());
                likeRepository.save(like);
                log.debug("Beğeni tekrar aktif edildi. userId={}, targetId={}", currentUserId, request.getTargetId());
            }
        } else {
            // Yeni kayıt oluştur
            Like newLike = Like.builder()
                    .targetId(request.getTargetId())
                    .targetType(request.getTargetType())
                    .userId(currentUserId)
                    .status(LikeStatus.ACTIVE)
                    .build();
            likeRepository.save(newLike);
            log.debug("Yeni beğeni oluşturuldu. userId={}, targetId={}", currentUserId, request.getTargetId());
        }

        long likeCount = likeRepository.countByTargetIdAndTargetTypeAndStatus(
                request.getTargetId(), request.getTargetType(), LikeStatus.ACTIVE);

        return likeMapper.toStatusResponse(request.getTargetId(), request.getTargetType(), true, likeCount);
    }

    @Override
    public LikeStatusResponse unlike(LikeRequest request, UUID currentUserId) {
        log.debug("Unlike isteği: userId={}, targetId={}, targetType={}", currentUserId, request.getTargetId(), request.getTargetType());

        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(
                currentUserId, request.getTargetId(), request.getTargetType());

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.getStatus() == LikeStatus.ACTIVE) {
                like.setStatus(LikeStatus.REMOVED);
                like.setUpdatedAt(LocalDateTime.now());
                likeRepository.save(like);
                log.debug("Beğeni kaldırıldı. userId={}, targetId={}", currentUserId, request.getTargetId());
            }
        }
        // Kayıt yoksa hata vermek yerine mevcut durumu dön

        long likeCount = likeRepository.countByTargetIdAndTargetTypeAndStatus(
                request.getTargetId(), request.getTargetType(), LikeStatus.ACTIVE);

        return likeMapper.toStatusResponse(request.getTargetId(), request.getTargetType(), false, likeCount);
    }

    @Override
    public LikeStatusResponse toggle(LikeRequest request, UUID currentUserId) {
        log.debug("Toggle like isteği: userId={}, targetId={}, targetType={}", currentUserId, request.getTargetId(), request.getTargetType());

        boolean isCurrentlyLiked = likeRepository.existsByUserIdAndTargetIdAndTargetTypeAndStatus(
                currentUserId, request.getTargetId(), request.getTargetType(), LikeStatus.ACTIVE);

        if (isCurrentlyLiked) {
            return unlike(request, currentUserId);
        } else {
            return like(request, currentUserId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LikeCountResponse getLikeCount(UUID targetId, LikeTargetType targetType) {
        log.debug("Like count sorgusu: targetId={}, targetType={}", targetId, targetType);

        long count = likeRepository.countByTargetIdAndTargetTypeAndStatus(targetId, targetType, LikeStatus.ACTIVE);
        return likeMapper.toCountResponse(targetId, targetType, count);
    }

    @Override
    @Transactional(readOnly = true)
    public LikeStatusResponse getLikeStatus(UUID targetId, LikeTargetType targetType, UUID currentUserId) {
        log.debug("Like status sorgusu: userId={}, targetId={}, targetType={}", currentUserId, targetId, targetType);

        boolean liked = likeRepository.existsByUserIdAndTargetIdAndTargetTypeAndStatus(
                currentUserId, targetId, targetType, LikeStatus.ACTIVE);
        long count = likeRepository.countByTargetIdAndTargetTypeAndStatus(targetId, targetType, LikeStatus.ACTIVE);

        return likeMapper.toStatusResponse(targetId, targetType, liked, count);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LikedTargetResponse> getMyLikes(UUID currentUserId, LikeTargetType targetType, Pageable pageable) {
        log.debug("Kendi beğenilerimi listele: userId={}, targetType={}", currentUserId, targetType);

        Page<Like> likes;
        if (targetType != null) {
            likes = likeRepository.findByUserIdAndTargetTypeAndStatus(currentUserId, targetType, LikeStatus.ACTIVE, pageable);
        } else {
            likes = likeRepository.findByUserIdAndStatus(currentUserId, LikeStatus.ACTIVE, pageable);
        }

        return likes.map(likeMapper::toLikedTargetResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LikedTargetResponse> getUserLikes(UUID userId, LikeTargetType targetType, Pageable pageable) {
        log.debug("Kullanıcının beğenilerini listele: userId={}, targetType={}", userId, targetType);

        Page<Like> likes;
        if (targetType != null) {
            likes = likeRepository.findByUserIdAndTargetTypeAndStatus(userId, targetType, LikeStatus.ACTIVE, pageable);
        } else {
            likes = likeRepository.findByUserIdAndStatus(userId, LikeStatus.ACTIVE, pageable);
        }

        return likes.map(likeMapper::toLikedTargetResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BulkLikeCountResponse getBulkLikeCounts(BulkLikeCountRequest request) {
        log.debug("Bulk like count sorgusu: {} item", request.getItems().size());

        List<LikeCountResponse> counts = request.getItems().stream()
                .map(item -> {
                    long count = likeRepository.countByTargetIdAndTargetTypeAndStatus(
                            item.getTargetId(), item.getTargetType(), LikeStatus.ACTIVE);
                    return likeMapper.toCountResponse(item.getTargetId(), item.getTargetType(), count);
                })
                .collect(Collectors.toList());

        return BulkLikeCountResponse.builder().items(counts).build();
    }

    @Override
    public void removeLikesByTarget(UUID targetId, LikeTargetType targetType) {
        log.info("Hedef için beğeniler pasifleştiriliyor: targetId={}, targetType={}", targetId, targetType);

        int removedCount = likeRepository.softRemoveByTargetIdAndTargetType(targetId, targetType);
        log.info("{} beğeni kaydı pasifleştirildi. targetId={}, targetType={}", removedCount, targetId, targetType);
    }

    /**
     * Beğenilecek hedefin diğer serviste var olup olmadığını kontrol eder.
     */
    private void validateTargetExists(UUID targetId, LikeTargetType targetType) {
        boolean exists = switch (targetType) {
            case POST -> postServiceClient.existsById(targetId);
            case COMMENT -> commentServiceClient.existsById(targetId);
            case STORY -> storyServiceClient.existsById(targetId);
            case EVENT -> eventServiceClient.existsById(targetId);
            case MEDIA -> mediaServiceClient.existsById(targetId);
        };

        if (!exists) {
            throw new InvalidLikeTargetException(
                    String.format("Beğenilecek hedef bulunamadı: %s (id: %s)", targetType, targetId));
        }
    }
}

package like_service.mapper;

import like_service.dto.response.*;
import like_service.entity.Like;
import like_service.entity.LikeStatus;
import like_service.entity.LikeTargetType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Entity <-> DTO dönüşümleri.
 */
@Component
public class LikeMapper {

    /**
     * Like entity -> LikeResponse
     */
    public LikeResponse toResponse(Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .userId(like.getUserId())
                .status(like.getStatus())
                .createdAt(like.getCreatedAt())
                .updatedAt(like.getUpdatedAt())
                .build();
    }

    /**
     * Like entity -> LikedTargetResponse
     */
    public LikedTargetResponse toLikedTargetResponse(Like like) {
        return LikedTargetResponse.builder()
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .likedAt(like.getCreatedAt())
                .build();
    }

    /**
     * LikeCountResponse oluştur.
     */
    public LikeCountResponse toCountResponse(UUID targetId, LikeTargetType targetType, long likeCount) {
        return LikeCountResponse.builder()
                .targetId(targetId)
                .targetType(targetType)
                .likeCount(likeCount)
                .build();
    }

    /**
     * LikeStatusResponse oluştur.
     */
    public LikeStatusResponse toStatusResponse(UUID targetId, LikeTargetType targetType, boolean likedByCurrentUser, long likeCount) {
        return LikeStatusResponse.builder()
                .targetId(targetId)
                .targetType(targetType)
                .likedByCurrentUser(likedByCurrentUser)
                .likeCount(likeCount)
                .build();
    }
}

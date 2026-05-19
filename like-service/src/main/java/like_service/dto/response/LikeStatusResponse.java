package like_service.dto.response;

import like_service.entity.LikeTargetType;
import lombok.*;

import java.util.UUID;

/**
 * Beğeni durumu response.
 * Kullanıcının bir içeriği beğenip beğenmediğini ve güncel like sayısını içerir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeStatusResponse {

    private UUID targetId;
    private LikeTargetType targetType;
    private boolean likedByCurrentUser;
    private long likeCount;
}

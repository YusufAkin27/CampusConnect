package like_service.dto.response;

import like_service.entity.LikeTargetType;
import lombok.*;

import java.util.UUID;

/**
 * Beğeni sayısı response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCountResponse {

    private UUID targetId;
    private LikeTargetType targetType;
    private long likeCount;
}

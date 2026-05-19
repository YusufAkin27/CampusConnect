package like_service.dto.response;

import like_service.entity.LikeTargetType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kullanıcının beğendiği içerik listesi response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedTargetResponse {

    private UUID targetId;
    private LikeTargetType targetType;
    private LocalDateTime likedAt;
}

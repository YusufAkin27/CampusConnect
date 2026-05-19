package like_service.dto.response;

import like_service.entity.LikeStatus;
import like_service.entity.LikeTargetType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Beğeni detay response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {

    private UUID id;
    private UUID targetId;
    private LikeTargetType targetType;
    private UUID userId;
    private LikeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

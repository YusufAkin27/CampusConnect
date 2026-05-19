package like_service.dto.request;

import jakarta.validation.constraints.NotNull;
import like_service.entity.LikeTargetType;
import lombok.*;

import java.util.UUID;

/**
 * İçerik beğenme/beğeni kaldırma isteği.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequest {

    @NotNull(message = "targetId boş olamaz")
    private UUID targetId;

    @NotNull(message = "targetType boş olamaz")
    private LikeTargetType targetType;
}

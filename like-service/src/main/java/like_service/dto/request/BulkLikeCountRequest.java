package like_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import like_service.entity.LikeTargetType;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Toplu beğeni sayısı sorgusu isteği.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLikeCountRequest {

    @NotEmpty(message = "items listesi boş olamaz")
    @Valid
    private List<TargetItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TargetItem {

        @NotNull(message = "targetId boş olamaz")
        private UUID targetId;

        @NotNull(message = "targetType boş olamaz")
        private LikeTargetType targetType;
    }
}

package like_service.dto.response;

import lombok.*;

import java.util.List;

/**
 * Toplu beğeni sayısı response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkLikeCountResponse {

    private List<LikeCountResponse> items;
}

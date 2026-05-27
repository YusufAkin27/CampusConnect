package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Compact user summary response used in search results and lists.
 * Contains minimal information needed to display a user card.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String displayName;
    private String profileImageUrl;

}

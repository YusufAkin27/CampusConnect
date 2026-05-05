package friend_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing a mutual friend between two users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutualFriendResponse {

    private UserSummaryResponse user;
    private LocalDateTime friendshipCreatedAt;
}

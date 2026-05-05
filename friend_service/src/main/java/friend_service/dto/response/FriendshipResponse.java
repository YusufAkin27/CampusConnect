package friend_service.dto.response;

import friend_service.enums.FriendshipStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing an active friendship with the friend's profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {

    private Long id;
    private UserSummaryResponse friend;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}

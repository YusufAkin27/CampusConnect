package friend_service.dto.response;

import friend_service.enums.FriendRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing a friend request with full sender/receiver details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {

    private Long id;
    private UserSummaryResponse sender;
    private UserSummaryResponse receiver;
    private FriendRequestStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime respondedAt;
}

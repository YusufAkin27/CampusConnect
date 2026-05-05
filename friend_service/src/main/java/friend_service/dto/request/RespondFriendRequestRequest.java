package friend_service.dto.request;

import friend_service.enums.FriendRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for responding to a pending friend request.
 *
 * Only ACCEPTED or REJECTED are valid statuses for this action.
 * PENDING, CANCELLED, and EXPIRED are not allowed here.
 */
@Data
public class RespondFriendRequestRequest {

    @NotNull(message = "status must not be null")
    private FriendRequestStatus status;
}

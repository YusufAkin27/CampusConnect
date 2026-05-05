package friend_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for sending a friend request.
 */
@Data
public class SendFriendRequestRequest {

    @NotNull(message = "receiverAuthUserId must not be null")
    private Long receiverAuthUserId;

    @Size(max = 250, message = "Message must not exceed 250 characters")
    private String message;
}

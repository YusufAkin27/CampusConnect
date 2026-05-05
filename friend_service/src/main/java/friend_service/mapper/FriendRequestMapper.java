package friend_service.mapper;

import friend_service.dto.response.FriendRequestResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.FriendRequest;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for FriendRequest entity to response DTOs.
 */
@Component
public class FriendRequestMapper {

    /**
     * Maps a FriendRequest entity to FriendRequestResponse,
     * enriched with full sender and receiver profile summaries.
     *
     * @param request  the FriendRequest entity
     * @param sender   the sender's UserSummaryResponse (from user-service or fallback)
     * @param receiver the receiver's UserSummaryResponse (from user-service or fallback)
     * @return the populated FriendRequestResponse
     */
    public FriendRequestResponse toFriendRequestResponse(
            FriendRequest request,
            UserSummaryResponse sender,
            UserSummaryResponse receiver
    ) {
        return FriendRequestResponse.builder()
                .id(request.getId())
                .sender(sender)
                .receiver(receiver)
                .status(request.getStatus())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .respondedAt(request.getRespondedAt())
                .build();
    }
}

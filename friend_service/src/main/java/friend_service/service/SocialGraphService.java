package friend_service.service;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.dto.response.InternalFriendStatusResponse;
import friend_service.dto.response.RelationStatusResponse;
import friend_service.dto.response.SocialStatsResponse;
import friend_service.dto.response.UserRelationResponse;

/**
 * Service interface for cross-cutting social graph operations:
 * - User search with relation context
 * - Relation status resolution
 * - Social statistics
 * - Internal status for inter-service use
 */
public interface SocialGraphService {

    /**
     * Searches users via user-service and enriches results with social relation data.
     */
    DataResponseMessage<PageResponse<UserRelationResponse>> searchUsersWithRelation(
            Long authUserId,
            String keyword,
            String faculty,
            String department,
            String grade,
            int page,
            int size
    );

    /**
     * Returns the complete social relation status between two users.
     */
    DataResponseMessage<RelationStatusResponse> getRelationStatus(
            Long requesterAuthUserId,
            Long targetAuthUserId
    );

    /**
     * Returns aggregated social statistics for a user.
     */
    DataResponseMessage<SocialStatsResponse> getSocialStats(Long authUserId);

    /**
     * Returns a lightweight friend/follow status for inter-service use.
     * Used by post-service, notification-service, etc.
     */
    DataResponseMessage<InternalFriendStatusResponse> getInternalFriendStatus(
            Long requesterAuthUserId,
            Long targetAuthUserId
    );
}

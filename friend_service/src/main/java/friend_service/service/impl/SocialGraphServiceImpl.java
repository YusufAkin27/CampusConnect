package friend_service.service.impl;

import friend_service.client.UserServiceClient;
import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.dto.response.*;
import friend_service.enums.FriendRelationStatus;
import friend_service.enums.FriendRequestStatus;
import friend_service.enums.FriendshipStatus;
import friend_service.enums.FollowStatus;
import friend_service.exception.InvalidRelationOperationException;
import friend_service.mapper.SocialGraphMapper;
import friend_service.repository.FollowRepository;
import friend_service.repository.FriendRequestRepository;
import friend_service.repository.FriendshipRepository;
import friend_service.service.SocialGraphService;
import friend_service.util.FriendshipKeyUtil;
import friend_service.util.RelationStatusResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of SocialGraphService.
 *
 * Handles cross-cutting social graph queries:
 * - User search with relation enrichment
 * - Relation status resolution
 * - Social statistics aggregation
 * - Internal inter-service status endpoint
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialGraphServiceImpl implements SocialGraphService {

    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserServiceClient userServiceClient;
    private final SocialGraphMapper socialGraphMapper;
    private final RelationStatusResolver relationStatusResolver;

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<UserRelationResponse>> searchUsersWithRelation(
            Long authUserId,
            String keyword,
            String faculty,
            String department,
            String grade,
            int page,
            int size
    ) {
        // Delegate search to user-service
        PageResponse<UserSummaryResponse> userPage = userServiceClient.searchUsers(
                keyword, faculty, department, grade, page, size);

        List<UserRelationResponse> enrichedResults = new ArrayList<>();

        for (UserSummaryResponse user : userPage.getContent()) {
            Long targetId = user.getAuthUserId();

            // Exclude self from results
            if (authUserId.equals(targetId)) continue;

            RelationContext ctx = buildRelationContext(authUserId, targetId);
            FriendRelationStatus status = relationStatusResolver.resolve(
                    ctx.isFriend, ctx.requestSent, ctx.requestReceived,
                    ctx.followingTarget, ctx.followedByTarget
            );

            UserRelationResponse response = socialGraphMapper.toUserRelationResponse(
                    user, status,
                    ctx.isFriend, ctx.requestSent, ctx.requestReceived,
                    ctx.followingTarget, ctx.followingTarget, ctx.followedByTarget,
                    ctx.mutualFriendCount
            );
            enrichedResults.add(response);
        }

        PageResponse<UserRelationResponse> enrichedPage = PageResponse.<UserRelationResponse>builder()
                .content(enrichedResults)
                .page(userPage.getPage())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();

        return DataResponseMessage.success("Users retrieved with relation status.", enrichedPage);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<RelationStatusResponse> getRelationStatus(
            Long requesterAuthUserId,
            Long targetAuthUserId
    ) {
        if (requesterAuthUserId.equals(targetAuthUserId)) {
            throw new InvalidRelationOperationException(
                    "Cannot get relation status between the same user.");
        }

        RelationContext ctx = buildRelationContext(requesterAuthUserId, targetAuthUserId);
        FriendRelationStatus status = relationStatusResolver.resolve(
                ctx.isFriend, ctx.requestSent, ctx.requestReceived,
                ctx.followingTarget, ctx.followedByTarget
        );

        RelationStatusResponse response = socialGraphMapper.toRelationStatusResponse(
                requesterAuthUserId, targetAuthUserId,
                status,
                ctx.isFriend, ctx.requestSent, ctx.requestReceived,
                ctx.followingTarget, ctx.followedByTarget,
                ctx.mutualFriendCount
        );

        return DataResponseMessage.success("Relation status retrieved.", response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<SocialStatsResponse> getSocialStats(Long authUserId) {
        Long friendCount = friendshipRepository.countByUserOneAuthUserIdAndStatusOrUserTwoAuthUserIdAndStatus(
                authUserId, FriendshipStatus.ACTIVE, authUserId, FriendshipStatus.ACTIVE);
        Long followerCount = followRepository.countByFollowingAuthUserIdAndStatus(authUserId, FollowStatus.ACTIVE);
        Long followingCount = followRepository.countByFollowerAuthUserIdAndStatus(authUserId, FollowStatus.ACTIVE);
        Long pendingReceived = friendRequestRepository.countByReceiverAuthUserIdAndStatus(
                authUserId, FriendRequestStatus.PENDING);
        Long pendingSent = friendRequestRepository.countBySenderAuthUserIdAndStatus(
                authUserId, FriendRequestStatus.PENDING);

        SocialStatsResponse stats = socialGraphMapper.toSocialStatsResponse(
                authUserId, friendCount, followerCount, followingCount, pendingReceived, pendingSent);

        return DataResponseMessage.success("Social stats retrieved.", stats);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<InternalFriendStatusResponse> getInternalFriendStatus(
            Long requesterAuthUserId,
            Long targetAuthUserId
    ) {
        RelationContext ctx = buildRelationContext(requesterAuthUserId, targetAuthUserId);

        InternalFriendStatusResponse response = InternalFriendStatusResponse.builder()
                .requesterAuthUserId(requesterAuthUserId)
                .targetAuthUserId(targetAuthUserId)
                .friends(ctx.isFriend)
                .following(ctx.followingTarget)
                .followsMe(ctx.followedByTarget)
                .mutualFriendCount(ctx.mutualFriendCount)
                .build();

        return DataResponseMessage.success("Internal friend status retrieved.", response);
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    /**
     * Builds a comprehensive relation context between two users by querying all relevant repos.
     */
    private RelationContext buildRelationContext(Long requesterId, Long targetId) {
        Long u1 = FriendshipKeyUtil.normalizeUserOne(requesterId, targetId);
        Long u2 = FriendshipKeyUtil.normalizeUserTwo(requesterId, targetId);

        boolean isFriend = friendshipRepository.existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
                u1, u2, FriendshipStatus.ACTIVE);

        boolean requestSent = friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                requesterId, targetId, FriendRequestStatus.PENDING);
        boolean requestReceived = friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                targetId, requesterId, FriendRequestStatus.PENDING);

        boolean followingTarget = followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                requesterId, targetId, FollowStatus.ACTIVE);
        boolean followedByTarget = followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                targetId, requesterId, FollowStatus.ACTIVE);

        Long mutualFriendCount = friendshipRepository.countMutualFriends(requesterId, targetId);

        return new RelationContext(isFriend, requestSent, requestReceived,
                followingTarget, followedByTarget, mutualFriendCount);
    }

    /**
     * Value object holding all relation flags for a user pair.
     */
    private record RelationContext(
            boolean isFriend,
            boolean requestSent,
            boolean requestReceived,
            boolean followingTarget,
            boolean followedByTarget,
            Long mutualFriendCount
    ) {}
}

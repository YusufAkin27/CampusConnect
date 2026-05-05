package friend_service.service.impl;

import friend_service.client.UserServiceClient;
import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FollowResponse;
import friend_service.dto.response.FollowerResponse;
import friend_service.dto.response.FollowingResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.Follow;
import friend_service.enums.FollowStatus;
import friend_service.enums.FriendshipStatus;
import friend_service.exception.InvalidFollowException;
import friend_service.mapper.FollowMapper;
import friend_service.repository.FollowRepository;
import friend_service.repository.FriendshipRepository;
import friend_service.service.FollowService;
import friend_service.util.FriendshipKeyUtil;
import friend_service.util.PageResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of FollowService.
 *
 * Follow is independent from friendship.
 * Unfollowing is soft-delete (status = UNFOLLOWED).
 * Re-following reactivates the existing record.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserServiceClient userServiceClient;
    private final FollowMapper followMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<FollowResponse> followUser(
            Long followerAuthUserId,
            Long followingAuthUserId
    ) {
        // Self-follow guard
        if (followerAuthUserId.equals(followingAuthUserId)) {
            throw new InvalidFollowException("You cannot follow yourself.");
        }

        // TODO (block-service integration): Check if either user has blocked the other
        //   - If followerAuthUserId is blocked by followingAuthUserId, deny follow
        //   - If followerAuthUserId has blocked followingAuthUserId, deny follow

        Optional<Follow> existing = followRepository
                .findByFollowerAuthUserIdAndFollowingAuthUserId(followerAuthUserId, followingAuthUserId);

        Follow follow;
        if (existing.isPresent()) {
            follow = existing.get();
            if (follow.getStatus() == FollowStatus.ACTIVE) {
                // Already following - return current state gracefully
                UserSummaryResponse follower = userServiceClient.getUserByAuthUserId(followerAuthUserId);
                UserSummaryResponse following = userServiceClient.getUserByAuthUserId(followingAuthUserId);
                return DataResponseMessage.success(
                        "You are already following this user.",
                        followMapper.toFollowResponse(follow, follower, following)
                );
            }
            // Reactivate unfollowed record
            follow.setStatus(FollowStatus.ACTIVE);
            follow.setUnfollowedAt(null);
            follow = followRepository.save(follow);
        } else {
            // Create new follow
            follow = followRepository.save(Follow.builder()
                    .followerAuthUserId(followerAuthUserId)
                    .followingAuthUserId(followingAuthUserId)
                    .status(FollowStatus.ACTIVE)
                    .build());
        }

        UserSummaryResponse follower = userServiceClient.getUserByAuthUserId(followerAuthUserId);
        UserSummaryResponse following = userServiceClient.getUserByAuthUserId(followingAuthUserId);

        log.info("User {} followed user {}", followerAuthUserId, followingAuthUserId);
        return DataResponseMessage.success(
                "Successfully followed user.",
                followMapper.toFollowResponse(follow, follower, following)
        );
    }

    @Override
    @Transactional
    public ResponseMessage unfollowUser(Long followerAuthUserId, Long followingAuthUserId) {
        Optional<Follow> existing = followRepository
                .findByFollowerAuthUserIdAndFollowingAuthUserId(followerAuthUserId, followingAuthUserId);

        if (existing.isEmpty() || existing.get().getStatus() == FollowStatus.UNFOLLOWED) {
            return ResponseMessage.success("You are not currently following this user.");
        }

        Follow follow = existing.get();
        follow.setStatus(FollowStatus.UNFOLLOWED);
        follow.setUnfollowedAt(LocalDateTime.now());
        followRepository.save(follow);

        log.info("User {} unfollowed user {}", followerAuthUserId, followingAuthUserId);
        return ResponseMessage.success("Successfully unfollowed user.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FollowerResponse>> getFollowers(
            Long authUserId,
            int page,
            int size
    ) {
        Page<Follow> followPage = followRepository.findByFollowingAuthUserIdAndStatus(
                authUserId,
                FollowStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Page<FollowerResponse> responsePage = followPage.map(follow -> {
            Long followerAuthUserId = follow.getFollowerAuthUserId();
            UserSummaryResponse user = userServiceClient.getUserByAuthUserId(followerAuthUserId);

            // Relation context
            boolean isFriend = isActiveFriends(authUserId, followerAuthUserId);
            boolean followedByMe = followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                    authUserId, followerAuthUserId, FollowStatus.ACTIVE);
            boolean followsMe = true; // They follow me (this entry)
            Long mutualCount = friendshipRepository.countMutualFriends(authUserId, followerAuthUserId);

            return followMapper.toFollowerResponse(follow, user, isFriend, followedByMe, followsMe, mutualCount);
        });

        return DataResponseMessage.success(
                "Followers retrieved.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FollowingResponse>> getFollowing(
            Long authUserId,
            int page,
            int size
    ) {
        Page<Follow> followPage = followRepository.findByFollowerAuthUserIdAndStatus(
                authUserId,
                FollowStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Page<FollowingResponse> responsePage = followPage.map(follow -> {
            Long followingAuthUserId = follow.getFollowingAuthUserId();
            UserSummaryResponse user = userServiceClient.getUserByAuthUserId(followingAuthUserId);

            // Relation context
            boolean isFriend = isActiveFriends(authUserId, followingAuthUserId);
            boolean followedByMe = true; // I follow them (this entry)
            boolean followsMe = followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                    followingAuthUserId, authUserId, FollowStatus.ACTIVE);
            Long mutualCount = friendshipRepository.countMutualFriends(authUserId, followingAuthUserId);

            return followMapper.toFollowingResponse(follow, user, isFriend, followedByMe, followsMe, mutualCount);
        });

        return DataResponseMessage.success(
                "Following list retrieved.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<Boolean> isFollowing(
            Long followerAuthUserId,
            Long followingAuthUserId
    ) {
        boolean following = followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                followerAuthUserId, followingAuthUserId, FollowStatus.ACTIVE);
        return DataResponseMessage.success("Follow status checked.", following);
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    private boolean isActiveFriends(Long userA, Long userB) {
        Long u1 = FriendshipKeyUtil.normalizeUserOne(userA, userB);
        Long u2 = FriendshipKeyUtil.normalizeUserTwo(userA, userB);
        return friendshipRepository.existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
                u1, u2, FriendshipStatus.ACTIVE);
    }
}

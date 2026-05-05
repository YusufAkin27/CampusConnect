package friend_service.service.impl;

import friend_service.client.UserServiceClient;
import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.SendFriendRequestRequest;
import friend_service.dto.response.FriendRequestResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.FriendRequest;
import friend_service.entity.Friendship;
import friend_service.enums.FriendRequestStatus;
import friend_service.enums.FriendshipStatus;
import friend_service.enums.FollowStatus;
import friend_service.exception.*;
import friend_service.mapper.FriendRequestMapper;
import friend_service.repository.FriendRequestRepository;
import friend_service.repository.FriendshipRepository;
import friend_service.repository.FollowRepository;
import friend_service.service.FriendRequestService;
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
 * Implementation of FriendRequestService.
 *
 * Business rules enforced:
 * - Cannot send request to self
 * - Cannot send if already friends
 * - Cannot send if PENDING request already exists in same direction
 * - If PENDING request exists in reverse direction: auto-accept both
 * - CANCELLED/REJECTED requests can be re-sent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;
    private final UserServiceClient userServiceClient;
    private final FriendRequestMapper friendRequestMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<FriendRequestResponse> sendFriendRequest(
            Long senderAuthUserId,
            SendFriendRequestRequest request
    ) {
        Long receiverAuthUserId = request.getReceiverAuthUserId();

        // Self-request guard
        if (senderAuthUserId.equals(receiverAuthUserId)) {
            throw new InvalidFriendRequestException("You cannot send a friend request to yourself.");
        }

        // Already friends guard
        Long userOne = FriendshipKeyUtil.normalizeUserOne(senderAuthUserId, receiverAuthUserId);
        Long userTwo = FriendshipKeyUtil.normalizeUserTwo(senderAuthUserId, receiverAuthUserId);
        if (friendshipRepository.existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
                userOne, userTwo, FriendshipStatus.ACTIVE)) {
            throw new AlreadyFriendsException("You are already friends with this user.");
        }

        // Duplicate pending request guard (same direction)
        if (friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                senderAuthUserId, receiverAuthUserId, FriendRequestStatus.PENDING)) {
            throw new FriendRequestAlreadyExistsException(
                    "A pending friend request to this user already exists.");
        }

        // Reverse pending: if target already sent a request to sender, auto-accept
        Optional<FriendRequest> reverseRequest = friendRequestRepository
                .findBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                        receiverAuthUserId, senderAuthUserId, FriendRequestStatus.PENDING);

        if (reverseRequest.isPresent()) {
            FriendRequest existing = reverseRequest.get();
            existing.setStatus(FriendRequestStatus.ACCEPTED);
            existing.setRespondedAt(LocalDateTime.now());
            friendRequestRepository.save(existing);

            // Create or reactivate friendship
            createOrReactivateFriendship(receiverAuthUserId, senderAuthUserId);

            // Optional: auto follow each other when becoming friends
            // autoFollowEachOther(senderAuthUserId, receiverAuthUserId);

            UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(existing.getSenderAuthUserId());
            UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(existing.getReceiverAuthUserId());
            return DataResponseMessage.success(
                    "Friend request auto-accepted (reverse request existed).",
                    friendRequestMapper.toFriendRequestResponse(existing, sender, receiver)
            );
        }

        // Create new friend request
        FriendRequest newRequest = FriendRequest.builder()
                .senderAuthUserId(senderAuthUserId)
                .receiverAuthUserId(receiverAuthUserId)
                .status(FriendRequestStatus.PENDING)
                .message(request.getMessage())
                .build();
        friendRequestRepository.save(newRequest);

        UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(senderAuthUserId);
        UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(receiverAuthUserId);

        log.info("Friend request sent from {} to {}", senderAuthUserId, receiverAuthUserId);
        return DataResponseMessage.success(
                "Friend request sent successfully.",
                friendRequestMapper.toFriendRequestResponse(newRequest, sender, receiver)
        );
    }

    @Override
    @Transactional
    public DataResponseMessage<FriendRequestResponse> acceptFriendRequest(
            Long receiverAuthUserId,
            Long requestId
    ) {
        FriendRequest request = friendRequestRepository.findByIdAndStatus(requestId, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(requestId));

        if (!request.getReceiverAuthUserId().equals(receiverAuthUserId)) {
            throw new FriendRequestAccessDeniedException(
                    "Only the receiver can accept a friend request.");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        createOrReactivateFriendship(request.getSenderAuthUserId(), request.getReceiverAuthUserId());

        // Optional: auto follow each other when becoming friends
        // autoFollowEachOther(request.getSenderAuthUserId(), request.getReceiverAuthUserId());

        UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(request.getSenderAuthUserId());
        UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(request.getReceiverAuthUserId());

        log.info("Friend request {} accepted by {}", requestId, receiverAuthUserId);
        return DataResponseMessage.success(
                "Friend request accepted.",
                friendRequestMapper.toFriendRequestResponse(request, sender, receiver)
        );
    }

    @Override
    @Transactional
    public DataResponseMessage<FriendRequestResponse> rejectFriendRequest(
            Long receiverAuthUserId,
            Long requestId
    ) {
        FriendRequest request = friendRequestRepository.findByIdAndStatus(requestId, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(requestId));

        if (!request.getReceiverAuthUserId().equals(receiverAuthUserId)) {
            throw new FriendRequestAccessDeniedException(
                    "Only the receiver can reject a friend request.");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(request.getSenderAuthUserId());
        UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(request.getReceiverAuthUserId());

        log.info("Friend request {} rejected by {}", requestId, receiverAuthUserId);
        return DataResponseMessage.success(
                "Friend request rejected.",
                friendRequestMapper.toFriendRequestResponse(request, sender, receiver)
        );
    }

    @Override
    @Transactional
    public ResponseMessage cancelFriendRequest(Long senderAuthUserId, Long requestId) {
        FriendRequest request = friendRequestRepository.findByIdAndStatus(requestId, FriendRequestStatus.PENDING)
                .orElseThrow(() -> new FriendRequestNotFoundException(requestId));

        if (!request.getSenderAuthUserId().equals(senderAuthUserId)) {
            throw new FriendRequestAccessDeniedException(
                    "Only the sender can cancel a friend request.");
        }

        request.setStatus(FriendRequestStatus.CANCELLED);
        request.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        log.info("Friend request {} cancelled by sender {}", requestId, senderAuthUserId);
        return ResponseMessage.success("Friend request cancelled.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FriendRequestResponse>> getReceivedRequests(
            Long receiverAuthUserId,
            int page,
            int size
    ) {
        Page<FriendRequest> requestPage = friendRequestRepository
                .findByReceiverAuthUserIdAndStatus(
                        receiverAuthUserId,
                        FriendRequestStatus.PENDING,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

        Page<FriendRequestResponse> responsePage = requestPage.map(req -> {
            UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(req.getSenderAuthUserId());
            UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(req.getReceiverAuthUserId());
            return friendRequestMapper.toFriendRequestResponse(req, sender, receiver);
        });

        return DataResponseMessage.success(
                "Received friend requests retrieved.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FriendRequestResponse>> getSentRequests(
            Long senderAuthUserId,
            int page,
            int size
    ) {
        Page<FriendRequest> requestPage = friendRequestRepository
                .findBySenderAuthUserIdAndStatus(
                        senderAuthUserId,
                        FriendRequestStatus.PENDING,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

        Page<FriendRequestResponse> responsePage = requestPage.map(req -> {
            UserSummaryResponse sender = userServiceClient.getUserByAuthUserId(req.getSenderAuthUserId());
            UserSummaryResponse receiver = userServiceClient.getUserByAuthUserId(req.getReceiverAuthUserId());
            return friendRequestMapper.toFriendRequestResponse(req, sender, receiver);
        });

        return DataResponseMessage.success(
                "Sent friend requests retrieved.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    /**
     * Creates a new Friendship or reactivates an existing REMOVED one.
     */
    private void createOrReactivateFriendship(Long userAAuthUserId, Long userBAuthUserId) {
        Long userOne = FriendshipKeyUtil.normalizeUserOne(userAAuthUserId, userBAuthUserId);
        Long userTwo = FriendshipKeyUtil.normalizeUserTwo(userAAuthUserId, userBAuthUserId);

        Optional<Friendship> existing = friendshipRepository
                .findByUserOneAuthUserIdAndUserTwoAuthUserId(userOne, userTwo);

        if (existing.isPresent()) {
            Friendship friendship = existing.get();
            friendship.setStatus(FriendshipStatus.ACTIVE);
            friendship.setRemovedAt(null);
            friendshipRepository.save(friendship);
        } else {
            Friendship newFriendship = Friendship.builder()
                    .userOneAuthUserId(userOne)
                    .userTwoAuthUserId(userTwo)
                    .status(FriendshipStatus.ACTIVE)
                    .build();
            friendshipRepository.save(newFriendship);
        }
    }

    /**
     * OPTIONAL: Auto-follow each other when becoming friends.
     * Uncomment usages in acceptFriendRequest and sendFriendRequest (reverse-accept) to enable.
     *
     * Each user gets an ACTIVE follow record towards the other if not already following.
     */
    @SuppressWarnings("unused")
    private void autoFollowEachOther(Long userA, Long userB) {
        ensureFollowExists(userA, userB);
        ensureFollowExists(userB, userA);
    }

    private void ensureFollowExists(Long follower, Long following) {
        followRepository.findByFollowerAuthUserIdAndFollowingAuthUserId(follower, following)
                .ifPresentOrElse(
                        follow -> {
                            if (follow.getStatus() != FollowStatus.ACTIVE) {
                                follow.setStatus(FollowStatus.ACTIVE);
                                follow.setUnfollowedAt(null);
                                followRepository.save(follow);
                            }
                        },
                        () -> followRepository.save(
                                friend_service.entity.Follow.builder()
                                        .followerAuthUserId(follower)
                                        .followingAuthUserId(following)
                                        .status(FollowStatus.ACTIVE)
                                        .build()
                        )
                );
    }
}

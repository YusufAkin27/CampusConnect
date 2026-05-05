package friend_service.service.impl;

import friend_service.client.UserServiceClient;
import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FriendshipResponse;
import friend_service.dto.response.MutualFriendResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.Friendship;
import friend_service.enums.FriendshipStatus;
import friend_service.exception.FriendshipNotFoundException;
import friend_service.exception.NotFriendsException;
import friend_service.mapper.FriendshipMapper;
import friend_service.repository.FriendshipRepository;
import friend_service.service.FriendshipService;
import friend_service.util.FriendshipKeyUtil;
import friend_service.util.PageResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FriendshipService.
 *
 * Friendship removal is soft-delete only (status = REMOVED, removedAt set).
 * Follow relationships are NOT affected by friendship removal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserServiceClient userServiceClient;
    private final FriendshipMapper friendshipMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public ResponseMessage removeFriend(Long authUserId, Long friendAuthUserId) {
        Long userOne = FriendshipKeyUtil.normalizeUserOne(authUserId, friendAuthUserId);
        Long userTwo = FriendshipKeyUtil.normalizeUserTwo(authUserId, friendAuthUserId);

        Friendship friendship = friendshipRepository
                .findByUserOneAuthUserIdAndUserTwoAuthUserId(userOne, userTwo)
                .orElseThrow(() -> new NotFriendsException("No friendship exists with this user."));

        if (friendship.getStatus() == FriendshipStatus.REMOVED) {
            throw new NotFriendsException("You are not currently friends with this user.");
        }

        friendship.setStatus(FriendshipStatus.REMOVED);
        friendship.setRemovedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);

        // Follow relationships remain unaffected (independent systems)
        // TODO (block-service integration): Check for active blocks before removing friend

        log.info("User {} removed friend {}", authUserId, friendAuthUserId);
        return ResponseMessage.success("Friend removed successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FriendshipResponse>> getMyFriends(
            Long authUserId,
            int page,
            int size
    ) {
        Page<Friendship> friendshipPage = friendshipRepository
                .findByUserOneAuthUserIdAndStatusOrUserTwoAuthUserIdAndStatus(
                        authUserId, FriendshipStatus.ACTIVE,
                        authUserId, FriendshipStatus.ACTIVE,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

        Page<FriendshipResponse> responsePage = friendshipPage.map(f -> {
            Long friendId = getFriendId(f, authUserId);
            UserSummaryResponse friend = userServiceClient.getUserByAuthUserId(friendId);
            return friendshipMapper.toFriendshipResponse(f, authUserId, friend);
        });

        return DataResponseMessage.success(
                "Friends retrieved successfully.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<FriendshipResponse>> getUserFriends(
            Long requesterAuthUserId,
            Long targetAuthUserId,
            int page,
            int size
    ) {
        // TODO: Add privacy check via user-service to verify the target's privacy settings
        //       before exposing their friend list to the requester.

        Page<Friendship> friendshipPage = friendshipRepository
                .findByUserOneAuthUserIdAndStatusOrUserTwoAuthUserIdAndStatus(
                        targetAuthUserId, FriendshipStatus.ACTIVE,
                        targetAuthUserId, FriendshipStatus.ACTIVE,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

        Page<FriendshipResponse> responsePage = friendshipPage.map(f -> {
            Long friendId = getFriendId(f, targetAuthUserId);
            UserSummaryResponse friend = userServiceClient.getUserByAuthUserId(friendId);
            return friendshipMapper.toFriendshipResponse(f, targetAuthUserId, friend);
        });

        return DataResponseMessage.success(
                "User's friends retrieved successfully.",
                pageResponseConverter.toPageResponse(responsePage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<Boolean> areFriends(Long firstAuthUserId, Long secondAuthUserId) {
        Long userOne = FriendshipKeyUtil.normalizeUserOne(firstAuthUserId, secondAuthUserId);
        Long userTwo = FriendshipKeyUtil.normalizeUserTwo(firstAuthUserId, secondAuthUserId);

        boolean friends = friendshipRepository
                .existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
                        userOne, userTwo, FriendshipStatus.ACTIVE);

        return DataResponseMessage.success("Friendship status checked.", friends);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<MutualFriendResponse>> getMutualFriends(
            Long requesterAuthUserId,
            Long targetAuthUserId,
            int page,
            int size
    ) {
        List<Long> mutualIds = friendshipRepository.getMutualFriendIds(
                requesterAuthUserId, targetAuthUserId);

        // Apply manual pagination
        int start = page * size;
        int end = Math.min(start + size, mutualIds.size());

        List<MutualFriendResponse> responses;
        if (start >= mutualIds.size()) {
            responses = List.of();
        } else {
            responses = mutualIds.subList(start, end).stream()
                    .map(id -> {
                        UserSummaryResponse user = userServiceClient.getUserByAuthUserId(id);
                        // Fetch friendship date from requester's perspective
                        Long u1 = FriendshipKeyUtil.normalizeUserOne(requesterAuthUserId, id);
                        Long u2 = FriendshipKeyUtil.normalizeUserTwo(requesterAuthUserId, id);
                        LocalDateTime createdAt = friendshipRepository
                                .findByUserOneAuthUserIdAndUserTwoAuthUserId(u1, u2)
                                .map(Friendship::getCreatedAt)
                                .orElse(null);
                        return friendshipMapper.toMutualFriendResponse(user, createdAt);
                    })
                    .collect(Collectors.toList());
        }

        int totalPages = (int) Math.ceil((double) mutualIds.size() / size);
        Page<MutualFriendResponse> resultPage = new PageImpl<>(responses,
                PageRequest.of(page, size), mutualIds.size());

        return DataResponseMessage.success(
                "Mutual friends retrieved.",
                pageResponseConverter.toPageResponse(resultPage)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<Long> getMutualFriendCount(
            Long requesterAuthUserId,
            Long targetAuthUserId
    ) {
        Long count = friendshipRepository.countMutualFriends(requesterAuthUserId, targetAuthUserId);
        return DataResponseMessage.success("Mutual friend count retrieved.", count);
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    /**
     * Determines the "other" user in a friendship from the perspective of currentAuthUserId.
     */
    private Long getFriendId(Friendship friendship, Long currentAuthUserId) {
        return friendship.getUserOneAuthUserId().equals(currentAuthUserId)
                ? friendship.getUserTwoAuthUserId()
                : friendship.getUserOneAuthUserId();
    }
}

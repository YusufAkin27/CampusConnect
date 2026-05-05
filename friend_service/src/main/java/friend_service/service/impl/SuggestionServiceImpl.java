package friend_service.service.impl;

import friend_service.client.UserServiceClient;
import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.IgnoreSuggestionRequest;
import friend_service.dto.response.SuggestedUserResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.FriendSuggestionIgnore;
import friend_service.enums.FriendRequestStatus;
import friend_service.enums.FriendshipStatus;
import friend_service.enums.FollowStatus;
import friend_service.enums.SuggestionReason;
import friend_service.exception.InvalidRelationOperationException;
import friend_service.mapper.SuggestionMapper;
import friend_service.repository.FriendRequestRepository;
import friend_service.repository.FriendSuggestionIgnoreRepository;
import friend_service.repository.FriendshipRepository;
import friend_service.repository.FollowRepository;
import friend_service.service.SuggestionService;
import friend_service.util.FriendshipKeyUtil;
import friend_service.util.SuggestionScoreCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of SuggestionService.
 *
 * Suggestion algorithm:
 * 1. Fetch a batch of candidates from user-service.
 * 2. Exclude: self, existing friends, PENDING request targets, ignored, already-following.
 * 3. Score each candidate using SuggestionScoreCalculator.
 * 4. Sort by score descending.
 * 5. Apply pagination.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private static final int CANDIDATE_BATCH_SIZE = 100;

    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendSuggestionIgnoreRepository ignoreRepository;
    private final UserServiceClient userServiceClient;
    private final SuggestionMapper suggestionMapper;
    private final SuggestionScoreCalculator scoreCalculator;

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<SuggestedUserResponse>> getSuggestedUsers(
            Long authUserId,
            int page,
            int size
    ) {
        // Fetch the current user's profile for comparison
        UserSummaryResponse currentUser = userServiceClient.getUserByAuthUserId(authUserId);

        // Collect exclusion sets
        Set<Long> friendIds = Set.copyOf(friendshipRepository.getFriendIds(authUserId));
        Set<Long> followingIds = Set.copyOf(followRepository.findFollowingIds(authUserId));

        Set<Long> ignoredIds = ignoreRepository.findByAuthUserId(authUserId).stream()
                .map(FriendSuggestionIgnore::getIgnoredAuthUserId)
                .collect(Collectors.toSet());

        // Fetch candidates from user-service (broad search, no filter)
        PageResponse<UserSummaryResponse> candidates = userServiceClient
                .searchUsers(null, null, null, null, 0, CANDIDATE_BATCH_SIZE);

        List<SuggestedUserResponse> suggestions = new ArrayList<>();

        for (UserSummaryResponse candidate : candidates.getContent()) {
            Long candidateId = candidate.getAuthUserId();

            // Exclude self, friends, ignored, and already-following
            if (candidateId == null) continue;
            if (candidateId.equals(authUserId)) continue;
            if (friendIds.contains(candidateId)) continue;
            if (ignoredIds.contains(candidateId)) continue;

            // Exclude users with PENDING requests in either direction
            boolean pendingSent = friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                    authUserId, candidateId, FriendRequestStatus.PENDING);
            boolean pendingReceived = friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                    candidateId, authUserId, FriendRequestStatus.PENDING);
            if (pendingSent || pendingReceived) continue;

            // Compute mutual friend count
            Long mutualCount = friendshipRepository.countMutualFriends(authUserId, candidateId);

            // Determine suggestion reason and score
            SuggestionReason reason = scoreCalculator.determinePrimaryReason(currentUser, candidate, mutualCount);
            boolean sameFaculty = scoreCalculator.isSameFaculty(currentUser, candidate);
            boolean sameDepartment = scoreCalculator.isSameDepartment(currentUser, candidate);
            boolean sameGrade = scoreCalculator.isSameGrade(currentUser, candidate);

            boolean followedByMe = followingIds.contains(candidateId);

            SuggestedUserResponse suggestion = suggestionMapper.toSuggestedUserResponse(
                    candidate,
                    reason,
                    mutualCount,
                    sameFaculty,
                    sameDepartment,
                    sameGrade,
                    followedByMe,
                    pendingSent,
                    pendingReceived,
                    false // Not friends (excluded above)
            );

            suggestions.add(suggestion);
        }

        // Sort by score descending
        suggestions.sort(Comparator.comparingInt((SuggestedUserResponse s) ->
                scoreCalculator.calculateScore(currentUser, s.getUser(),
                        s.getMutualFriendCount() != null ? s.getMutualFriendCount() : 0L)
        ).reversed());

        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, suggestions.size());
        List<SuggestedUserResponse> pagedContent = (start >= suggestions.size())
                ? List.of()
                : suggestions.subList(start, end);

        int totalPages = (int) Math.ceil((double) suggestions.size() / size);
        PageResponse<SuggestedUserResponse> response = PageResponse.<SuggestedUserResponse>builder()
                .content(pagedContent)
                .page(page)
                .size(size)
                .totalElements(suggestions.size())
                .totalPages(totalPages)
                .last(end >= suggestions.size())
                .build();

        return DataResponseMessage.success("Suggested users retrieved.", response);
    }

    @Override
    @Transactional
    public ResponseMessage ignoreSuggestion(Long authUserId, IgnoreSuggestionRequest request) {
        Long ignoredAuthUserId = request.getIgnoredAuthUserId();

        if (authUserId.equals(ignoredAuthUserId)) {
            throw new InvalidRelationOperationException("You cannot ignore yourself.");
        }

        if (ignoreRepository.existsByAuthUserIdAndIgnoredAuthUserId(authUserId, ignoredAuthUserId)) {
            return ResponseMessage.success("This suggestion is already hidden.");
        }

        ignoreRepository.save(FriendSuggestionIgnore.builder()
                .authUserId(authUserId)
                .ignoredAuthUserId(ignoredAuthUserId)
                .build());

        log.info("User {} ignored suggestion for {}", authUserId, ignoredAuthUserId);
        return ResponseMessage.success("Suggestion hidden successfully.");
    }

    @Override
    @Transactional
    public ResponseMessage undoIgnoreSuggestion(Long authUserId, Long ignoredAuthUserId) {
        if (!ignoreRepository.existsByAuthUserIdAndIgnoredAuthUserId(authUserId, ignoredAuthUserId)) {
            return ResponseMessage.success("No ignore record found for this user.");
        }

        ignoreRepository.deleteByAuthUserIdAndIgnoredAuthUserId(authUserId, ignoredAuthUserId);

        log.info("User {} restored suggestion for {}", authUserId, ignoredAuthUserId);
        return ResponseMessage.success("Suggestion restored successfully.");
    }
}

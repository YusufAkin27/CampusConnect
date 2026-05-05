package friend_service.config;

import friend_service.entity.Follow;
import friend_service.entity.FriendRequest;
import friend_service.entity.Friendship;
import friend_service.enums.FollowStatus;
import friend_service.enums.FriendRequestStatus;
import friend_service.enums.FriendshipStatus;
import friend_service.repository.FollowRepository;
import friend_service.repository.FriendRequestRepository;
import friend_service.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Development data initializer for friend-service.
 *
 * Controlled by app.seed.enabled property (default: false).
 * When enabled, creates sample friend requests, friendships, and follows.
 *
 * Uses fake authUserIds (1, 2, 3) that should correspond to users in user-service.
 * Duplicate records are skipped via existence checks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            log.info("Data seeding is disabled (app.seed.enabled=false). Skipping.");
            return;
        }

        log.info("Data seeding is ENABLED. Seeding sample friend-service data...");
        seedFriendRequests();
        seedFriendships();
        seedFollows();
        log.info("Data seeding complete.");
    }

    private void seedFriendRequests() {
        // Seed: User 1 -> User 3 (PENDING)
        if (!friendRequestRepository.existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
                1L, 3L, FriendRequestStatus.PENDING)) {
            friendRequestRepository.save(FriendRequest.builder()
                    .senderAuthUserId(1L)
                    .receiverAuthUserId(3L)
                    .status(FriendRequestStatus.PENDING)
                    .message("Hey! Let's connect!")
                    .build());
            log.info("Seeded: Friend request 1 -> 3");
        }
    }

    private void seedFriendships() {
        // Seed: User 1 & User 2 are friends (userOne < userTwo)
        if (!friendshipRepository.existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
                1L, 2L, FriendshipStatus.ACTIVE)) {
            friendshipRepository.save(Friendship.builder()
                    .userOneAuthUserId(1L)
                    .userTwoAuthUserId(2L)
                    .status(FriendshipStatus.ACTIVE)
                    .build());
            log.info("Seeded: Friendship 1 <-> 2");
        }
    }

    private void seedFollows() {
        // Seed: User 1 follows User 2
        if (!followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                1L, 2L, FollowStatus.ACTIVE)) {
            followRepository.save(Follow.builder()
                    .followerAuthUserId(1L)
                    .followingAuthUserId(2L)
                    .status(FollowStatus.ACTIVE)
                    .build());
            log.info("Seeded: Follow 1 -> 2");
        }

        // Seed: User 2 follows User 1 (mutual)
        if (!followRepository.existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
                2L, 1L, FollowStatus.ACTIVE)) {
            followRepository.save(Follow.builder()
                    .followerAuthUserId(2L)
                    .followingAuthUserId(1L)
                    .status(FollowStatus.ACTIVE)
                    .build());
            log.info("Seeded: Follow 2 -> 1");
        }
    }
}

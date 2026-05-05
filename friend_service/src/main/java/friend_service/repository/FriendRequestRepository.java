package friend_service.repository;

import friend_service.entity.FriendRequest;
import friend_service.enums.FriendRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    /**
     * Find a friend request by its ID and current status.
     */
    Optional<FriendRequest> findByIdAndStatus(Long id, FriendRequestStatus status);

    /**
     * Check if a PENDING request exists from sender to receiver in a specific direction.
     */
    boolean existsBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
            Long senderAuthUserId,
            Long receiverAuthUserId,
            FriendRequestStatus status
    );

    /**
     * Find the PENDING request from a sender to a receiver.
     */
    Optional<FriendRequest> findBySenderAuthUserIdAndReceiverAuthUserIdAndStatus(
            Long senderAuthUserId,
            Long receiverAuthUserId,
            FriendRequestStatus status
    );

    /**
     * Get all friend requests received by a user filtered by status, with pagination.
     */
    Page<FriendRequest> findByReceiverAuthUserIdAndStatus(
            Long receiverAuthUserId,
            FriendRequestStatus status,
            Pageable pageable
    );

    /**
     * Get all friend requests sent by a user filtered by status, with pagination.
     */
    Page<FriendRequest> findBySenderAuthUserIdAndStatus(
            Long senderAuthUserId,
            FriendRequestStatus status,
            Pageable pageable
    );

    /**
     * Count pending received friend requests.
     */
    Long countByReceiverAuthUserIdAndStatus(Long receiverAuthUserId, FriendRequestStatus status);

    /**
     * Count pending sent friend requests.
     */
    Long countBySenderAuthUserIdAndStatus(Long senderAuthUserId, FriendRequestStatus status);
}

package friend_service.repository;

import friend_service.entity.Follow;
import friend_service.enums.FollowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * Find a follow record between two users regardless of status.
     */
    Optional<Follow> findByFollowerAuthUserIdAndFollowingAuthUserId(
            Long followerAuthUserId,
            Long followingAuthUserId
    );

    /**
     * Check if an active follow relationship exists.
     */
    boolean existsByFollowerAuthUserIdAndFollowingAuthUserIdAndStatus(
            Long followerAuthUserId,
            Long followingAuthUserId,
            FollowStatus status
    );

    /**
     * Get all active follows for a user (people they follow).
     */
    Page<Follow> findByFollowerAuthUserIdAndStatus(
            Long followerAuthUserId,
            FollowStatus status,
            Pageable pageable
    );

    /**
     * Get all active followers of a user.
     */
    Page<Follow> findByFollowingAuthUserIdAndStatus(
            Long followingAuthUserId,
            FollowStatus status,
            Pageable pageable
    );

    /**
     * Count active follows for a user (how many they follow).
     */
    Long countByFollowerAuthUserIdAndStatus(Long followerAuthUserId, FollowStatus status);

    /**
     * Count active followers of a user.
     */
    Long countByFollowingAuthUserIdAndStatus(Long followingAuthUserId, FollowStatus status);

    /**
     * Returns all authUserIds that the given user actively follows.
     */
    @Query("SELECT f.followingAuthUserId FROM Follow f WHERE f.followerAuthUserId = :authUserId AND f.status = 'ACTIVE'")
    List<Long> findFollowingIds(@Param("authUserId") Long authUserId);
}

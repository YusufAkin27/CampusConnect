package friend_service.repository;

import friend_service.entity.Friendship;
import friend_service.enums.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * Find a friendship by the normalized user pair (userOne < userTwo).
     */
    Optional<Friendship> findByUserOneAuthUserIdAndUserTwoAuthUserId(
            Long userOneAuthUserId,
            Long userTwoAuthUserId
    );

    /**
     * Check if an active friendship exists between two users (normalized order).
     */
    boolean existsByUserOneAuthUserIdAndUserTwoAuthUserIdAndStatus(
            Long userOneAuthUserId,
            Long userTwoAuthUserId,
            FriendshipStatus status
    );

    /**
     * Get all friendships for a user (either side of the pair), filtered by status.
     */
    Page<Friendship> findByUserOneAuthUserIdAndStatusOrUserTwoAuthUserIdAndStatus(
            Long userOneAuthUserId,
            FriendshipStatus status1,
            Long userTwoAuthUserId,
            FriendshipStatus status2,
            Pageable pageable
    );

    /**
     * Count all friendships for a user (either side of the pair), filtered by status.
     */
    Long countByUserOneAuthUserIdAndStatusOrUserTwoAuthUserIdAndStatus(
            Long userOneAuthUserId,
            FriendshipStatus status1,
            Long userTwoAuthUserId,
            FriendshipStatus status2
    );

    /**
     * Returns all authUserIds that are friends with the given user (ACTIVE friendships only).
     */
    @Query("""
            SELECT CASE
                WHEN f.userOneAuthUserId = :authUserId THEN f.userTwoAuthUserId
                ELSE f.userOneAuthUserId
            END
            FROM Friendship f
            WHERE (f.userOneAuthUserId = :authUserId OR f.userTwoAuthUserId = :authUserId)
              AND f.status = 'ACTIVE'
            """)
    List<Long> getFriendIds(@Param("authUserId") Long authUserId);

    /**
     * Returns the authUserIds that are mutual friends between two users.
     */
    @Query("""
            SELECT CASE
                WHEN f1.userOneAuthUserId = :firstAuthUserId THEN f1.userTwoAuthUserId
                ELSE f1.userOneAuthUserId
            END
            FROM Friendship f1
            WHERE (f1.userOneAuthUserId = :firstAuthUserId OR f1.userTwoAuthUserId = :firstAuthUserId)
              AND f1.status = 'ACTIVE'
              AND (
                SELECT COUNT(f2) FROM Friendship f2
                WHERE (f2.userOneAuthUserId = :secondAuthUserId OR f2.userTwoAuthUserId = :secondAuthUserId)
                  AND f2.status = 'ACTIVE'
                  AND (
                    CASE WHEN f1.userOneAuthUserId = :firstAuthUserId THEN f1.userTwoAuthUserId ELSE f1.userOneAuthUserId END
                    IN (
                      CASE WHEN f2.userOneAuthUserId = :secondAuthUserId THEN f2.userTwoAuthUserId ELSE f2.userOneAuthUserId END
                    )
                  )
              ) > 0
            """)
    List<Long> getMutualFriendIds(
            @Param("firstAuthUserId") Long firstAuthUserId,
            @Param("secondAuthUserId") Long secondAuthUserId
    );

    /**
     * Counts the number of mutual friends between two users.
     */
    @Query("""
            SELECT COUNT(DISTINCT CASE
                WHEN f1.userOneAuthUserId = :firstAuthUserId THEN f1.userTwoAuthUserId
                ELSE f1.userOneAuthUserId
            END)
            FROM Friendship f1
            WHERE (f1.userOneAuthUserId = :firstAuthUserId OR f1.userTwoAuthUserId = :firstAuthUserId)
              AND f1.status = 'ACTIVE'
              AND (
                CASE WHEN f1.userOneAuthUserId = :firstAuthUserId THEN f1.userTwoAuthUserId ELSE f1.userOneAuthUserId END
              ) IN (
                SELECT CASE
                    WHEN f2.userOneAuthUserId = :secondAuthUserId THEN f2.userTwoAuthUserId
                    ELSE f2.userOneAuthUserId
                END
                FROM Friendship f2
                WHERE (f2.userOneAuthUserId = :secondAuthUserId OR f2.userTwoAuthUserId = :secondAuthUserId)
                  AND f2.status = 'ACTIVE'
              )
            """)
    Long countMutualFriends(
            @Param("firstAuthUserId") Long firstAuthUserId,
            @Param("secondAuthUserId") Long secondAuthUserId
    );
}

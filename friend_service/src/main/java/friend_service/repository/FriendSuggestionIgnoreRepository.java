package friend_service.repository;

import friend_service.entity.FriendSuggestionIgnore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendSuggestionIgnoreRepository extends JpaRepository<FriendSuggestionIgnore, Long> {

    /**
     * Check if a user has already ignored a specific suggestion target.
     */
    boolean existsByAuthUserIdAndIgnoredAuthUserId(Long authUserId, Long ignoredAuthUserId);

    /**
     * Get all ignored suggestions for a user (used to filter suggestion list).
     */
    List<FriendSuggestionIgnore> findByAuthUserId(Long authUserId);

    /**
     * Remove an ignored suggestion record.
     */
    void deleteByAuthUserIdAndIgnoredAuthUserId(Long authUserId, Long ignoredAuthUserId);
}

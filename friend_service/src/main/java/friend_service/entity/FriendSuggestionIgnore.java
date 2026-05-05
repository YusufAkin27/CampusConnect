package friend_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records a user's decision to hide a specific person from their suggested-users list.
 *
 * Rules:
 * - authUserId cannot equal ignoredAuthUserId.
 * - authUserId + ignoredAuthUserId must be unique (cannot ignore the same person twice).
 * - Ignored users are excluded from getSuggestedUsers results.
 */
@Entity
@Table(
        name = "friend_suggestion_ignores",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_suggestion_ignore_pair",
                        columnNames = {"auth_user_id", "ignored_auth_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_suggestion_ignore_auth_user", columnList = "auth_user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendSuggestionIgnore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who is hiding the suggestion.
     */
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;

    /**
     * The user being hidden from suggestions.
     */
    @Column(name = "ignored_auth_user_id", nullable = false)
    private Long ignoredAuthUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

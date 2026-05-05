package friend_service.entity;

import friend_service.enums.FollowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a directional follow relationship between two users.
 *
 * Rules:
 * - A user cannot follow themselves.
 * - followerAuthUserId + followingAuthUserId must be unique.
 * - Unfollowing is a soft-delete: status = UNFOLLOWED, unfollowedAt is set.
 * - Re-following reuses the existing row (status = ACTIVE).
 * - Follow is independent from friendship, but optionally auto-follow can be applied on friend accept.
 *
 * TODO: When block-service is integrated:
 *   - Prevent following if the target has blocked the follower or vice versa.
 *   - Auto-unfollow if a block is placed between users.
 */
@Entity
@Table(
        name = "follows",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_follow_pair",
                        columnNames = {"follower_auth_user_id", "following_auth_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_follow_follower", columnList = "follower_auth_user_id"),
                @Index(name = "idx_follow_following", columnList = "following_auth_user_id"),
                @Index(name = "idx_follow_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who is following.
     */
    @Column(name = "follower_auth_user_id", nullable = false)
    private Long followerAuthUserId;

    /**
     * The user being followed.
     */
    @Column(name = "following_auth_user_id", nullable = false)
    private Long followingAuthUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FollowStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set when the user unfollows. Null if still actively following.
     */
    @Column(name = "unfollowed_at")
    private LocalDateTime unfollowedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

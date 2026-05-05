package friend_service.entity;

import friend_service.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a confirmed mutual friendship between two users.
 *
 * Design note:
 * - userOneAuthUserId always stores the smaller authUserId.
 * - userTwoAuthUserId always stores the larger authUserId.
 * - This guarantees uniqueness: only one row per pair, regardless of who removed whom.
 * - Friendship removal is a soft delete: status = REMOVED, removedAt is set.
 * - Re-friending reuses the same row (status = ACTIVE).
 *
 * TODO: When block-service is integrated, check for active blocks before creating/restoring friendship.
 */
@Entity
@Table(
        name = "friendships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_friendship_user_pair",
                        columnNames = {"user_one_auth_user_id", "user_two_auth_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_friendship_user_one", columnList = "user_one_auth_user_id"),
                @Index(name = "idx_friendship_user_two", columnList = "user_two_auth_user_id"),
                @Index(name = "idx_friendship_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user with the lower authUserId in the pair.
     */
    @Column(name = "user_one_auth_user_id", nullable = false)
    private Long userOneAuthUserId;

    /**
     * The user with the higher authUserId in the pair.
     */
    @Column(name = "user_two_auth_user_id", nullable = false)
    private Long userTwoAuthUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendshipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set when the friendship is soft-deleted (status = REMOVED).
     */
    @Column(name = "removed_at")
    private LocalDateTime removedAt;

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

package friend_service.entity;

import friend_service.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a friend request between two users.
 *
 * Rules:
 * - senderAuthUserId cannot equal receiverAuthUserId.
 * - Only one PENDING request allowed between the same pair in the same direction.
 * - CANCELLED can only be performed by the sender.
 * - ACCEPTED/REJECTED can only be performed by the receiver.
 * - After ACCEPTED, a Friendship record must be created.
 */
@Entity
@Table(
        name = "friend_requests",
        indexes = {
                @Index(name = "idx_friend_req_sender", columnList = "sender_auth_user_id"),
                @Index(name = "idx_friend_req_receiver", columnList = "receiver_auth_user_id"),
                @Index(name = "idx_friend_req_status", columnList = "status"),
                @Index(name = "idx_friend_req_sender_receiver_status",
                        columnList = "sender_auth_user_id, receiver_auth_user_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who initiated the friend request.
     */
    @Column(name = "sender_auth_user_id", nullable = false)
    private Long senderAuthUserId;

    /**
     * The user who received the friend request.
     */
    @Column(name = "receiver_auth_user_id", nullable = false)
    private Long receiverAuthUserId;

    /**
     * Current lifecycle status of the request.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendRequestStatus status;

    /**
     * Optional short message attached to the request.
     * Maximum 250 characters.
     */
    @Column(name = "message", length = 250)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Timestamp when the receiver accepted or rejected the request,
     * or when the sender cancelled it.
     */
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

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

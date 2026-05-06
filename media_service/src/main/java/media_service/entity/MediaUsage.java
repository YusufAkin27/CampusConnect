package media_service.entity;

import jakarta.persistence.*;
import lombok.*;
import media_service.enums.MediaUsageStatus;

import java.time.LocalDateTime;

/**
 * Tracks which service and entity is using a specific media file.
 *
 * Example:
 *  - mediaId 44 is used by post-service for postId 12
 *  - mediaId 10 is used by user-service as a profile image for userId 5
 *
 * This allows orphaned media detection and cleanup in the future.
 */
@Entity
@Table(
    name = "media_usages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_media_usage",
            columnNames = {"media_file_id", "service_name", "target_type", "target_id"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MediaUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Reference to the MediaFile that is being used.
     * Stored as a simple Long to avoid cross-entity FK dependency issues.
     */
    @Column(name = "media_file_id", nullable = false)
    private Long mediaFileId;

    /**
     * Name of the service using this media (e.g. "post-service", "user-service").
     */
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    /**
     * Type of entity using this media (e.g. "POST", "USER_PROFILE", "EVENT").
     */
    @Column(name = "target_type", nullable = false)
    private String targetType;

    /**
     * ID of the target entity in the remote service.
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /**
     * AuthUserId of the user who triggered this usage.
     */
    @Column(name = "auth_user_id")
    private Long authUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MediaUsageStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = MediaUsageStatus.ACTIVE;
    }

    /**
     * Marks this usage as removed.
     */
    public void markRemoved() {
        this.status = MediaUsageStatus.REMOVED;
        this.removedAt = LocalDateTime.now();
    }
}

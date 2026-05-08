package story_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import story_service.enums.MediaType;
import story_service.enums.StoryStatus;
import story_service.enums.StoryVisibility;

@Entity
@Table(name = "stories", indexes = {
    @Index(name = "idx_story_owner_user_id", columnList = "ownerUserId"),
    @Index(name = "idx_story_status", columnList = "status"),
    @Index(name = "idx_story_expires_at", columnList = "expiresAt"),
    @Index(name = "idx_story_owner_status_expires", columnList = "ownerUserId, status, expiresAt"),
    @Index(name = "idx_story_created_at", columnList = "createdAt")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, length = 100)
    private String ownerUsername;

    @Column(nullable = false)
    private Long mediaId;

    @Column(length = 500)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MediaType mediaType;

    @Column(length = 300)
    private String caption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StoryVisibility visibility = StoryVisibility.FOLLOWERS_ONLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StoryStatus status = StoryStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime deletedAt;

    private String deletedBy;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusHours(24);
        }
        if (this.status == null) {
            this.status = StoryStatus.ACTIVE;
        }
        if (this.visibility == null) {
            this.visibility = StoryVisibility.FOLLOWERS_ONLY;
        }
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isActive() {
        return this.status == StoryStatus.ACTIVE && !isExpired() && !isDeleted();
    }

    public boolean isOwnedBy(Long userId) {
        return this.ownerUserId != null && this.ownerUserId.equals(userId);
    }
}

package story_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "story_views",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_story_viewer", columnNames = {"storyId", "viewerUserId"})
    },
    indexes = {
        @Index(name = "idx_story_view_story_id", columnList = "storyId"),
        @Index(name = "idx_story_view_viewer", columnList = "viewerUserId"),
        @Index(name = "idx_story_view_viewed_at", columnList = "viewedAt")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID storyId;

    @Column(nullable = false)
    private Long viewerUserId;

    @Column(nullable = false, length = 100)
    private String viewerUsername;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        if (this.viewedAt == null) {
            this.viewedAt = LocalDateTime.now();
        }
    }
}

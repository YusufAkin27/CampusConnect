package post_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hashtags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Stored in lowercase, without the leading '#' character.
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "usage_count")
    private Long usageCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.usageCount == null) this.usageCount = 0L;
        if (this.name != null) this.name = this.name.toLowerCase();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementUsageCount() {
        this.usageCount = this.usageCount == null ? 1L : this.usageCount + 1;
    }

    public void decrementUsageCount() {
        this.usageCount = (this.usageCount == null || this.usageCount <= 0) ? 0L : this.usageCount - 1;
    }
}

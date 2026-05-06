package logging_service.entity;

import jakarta.persistence.*;
import logging_service.enums.LogCategory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Log retention policy entity.
 * Defines how long logs of each category should be retained.
 *
 * Default retention values:
 * - API_REQUEST: 30 days
 * - APPLICATION: 60 days
 * - ERROR: 180 days
 * - SECURITY: 365 days
 * - AUDIT: 730 days
 * - BUSINESS: 180 days
 * - SYSTEM: 90 days
 * - AUTH: 365 days
 * - DATABASE: 90 days
 * - EXTERNAL_SERVICE: 30 days
 */
@Entity
@Table(
        name = "log_retention_policies",
        indexes = {
                @Index(name = "idx_retention_category", columnList = "category", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogRetentionPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private LogCategory category;

    /**
     * Number of days to retain logs for this category.
     * Minimum: 1 day.
     */
    @Column(name = "retention_days", nullable = false)
    private Integer retentionDays;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

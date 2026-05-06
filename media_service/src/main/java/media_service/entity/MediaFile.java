package media_service.entity;

import jakarta.persistence.*;
import lombok.*;
import media_service.enums.MediaContext;
import media_service.enums.MediaStatus;
import media_service.enums.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "media_files",
    indexes = {
        @Index(name = "idx_media_owner", columnList = "owner_auth_user_id"),
        @Index(name = "idx_media_type", columnList = "media_type"),
        @Index(name = "idx_media_context", columnList = "media_context"),
        @Index(name = "idx_media_status", columnList = "status"),
        @Index(name = "idx_media_created_at", columnList = "created_at"),
        @Index(name = "idx_media_owner_context", columnList = "owner_auth_user_id,media_context")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"variants", "usages"})
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The authUserId of the user who uploaded this file.
     */
    @Column(name = "owner_auth_user_id", nullable = false)
    private Long ownerAuthUserId;

    /**
     * Original filename as provided by the client.
     */
    @Column(name = "original_filename")
    private String originalFilename;

    /**
     * Secure filename stored in the system (UUID-based).
     */
    @Column(name = "stored_filename")
    private String storedFilename;

    /**
     * Path key within the storage system (e.g. posts/5/uuid-image.jpg).
     * Must be unique.
     */
    @Column(name = "storage_key", unique = true)
    private String storageKey;

    /**
     * Publicly accessible URL of the file.
     */
    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    /**
     * Thumbnail URL (nullable). For first version can equal mediaUrl for images.
     */
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_context", nullable = false)
    private MediaContext mediaContext;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MediaStatus status;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "extension")
    private String extension;

    /**
     * File size in bytes.
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Image width in pixels.
     */
    @Column(name = "width")
    private Integer width;

    /**
     * Image height in pixels.
     */
    @Column(name = "height")
    private Integer height;

    /**
     * Duration in seconds for video/audio files.
     */
    @Column(name = "duration")
    private Double duration;

    /**
     * SHA-256 checksum for duplicate detection (ready for future use).
     */
    @Column(name = "checksum")
    private String checksum;

    /**
     * Whether the file is publicly accessible.
     */
    @Column(name = "public_accessible")
    private Boolean publicAccessible;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relations
    @OneToMany(mappedBy = "mediaFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MediaVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "mediaFileId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MediaUsage> usages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = MediaStatus.ACTIVE;
        if (this.publicAccessible == null) this.publicAccessible = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete - sets status to DELETED and records deletion time.
     */
    public void softDelete() {
        this.status = MediaStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}

package media_service.entity;

import jakarta.persistence.*;
import lombok.*;
import media_service.enums.MediaVariantType;

import java.time.LocalDateTime;

/**
 * Stores thumbnail, compressed, resized, and other variant files.
 * In the first version, actual resize/thumbnail generation is NOT required,
 * but the structure is ready for future implementation.
 *
 * TODO: Implement actual image resizing using ImageIO or a library like Thumbnailator.
 * TODO: Implement video thumbnail extraction.
 */
@Entity
@Table(name = "media_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "mediaFile")
public class MediaVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", nullable = false)
    private MediaFile mediaFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "variant_type", nullable = false)
    private MediaVariantType variantType;

    @Column(name = "variant_url", nullable = false)
    private String variantUrl;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    /**
     * File size in bytes.
     */
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

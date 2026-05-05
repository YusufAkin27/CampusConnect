package post_service.entity;

import jakarta.persistence.*;
import lombok.*;
import post_service.enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"mediaList", "comments", "postLikes", "savedPosts", "postHashtags", "mentions", "postReports"})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private PostVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @Column(name = "pinned")
    private Boolean pinned;

    @Column(name = "comments_enabled")
    private Boolean commentsEnabled;

    @Column(name = "likes_enabled")
    private Boolean likesEnabled;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "save_count")
    private Long saveCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "share_count")
    private Long shareCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relations
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SavedPost> savedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostHashtag> postHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Mention> mentions = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PostReport> postReports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = PostStatus.ACTIVE;
        if (this.visibility == null) this.visibility = PostVisibility.PUBLIC;
        if (this.pinned == null) this.pinned = false;
        if (this.commentsEnabled == null) this.commentsEnabled = true;
        if (this.likesEnabled == null) this.likesEnabled = true;
        if (this.likeCount == null) this.likeCount = 0L;
        if (this.commentCount == null) this.commentCount = 0L;
        if (this.saveCount == null) this.saveCount = 0L;
        if (this.viewCount == null) this.viewCount = 0L;
        if (this.shareCount == null) this.shareCount = 0L;
        if (this.reportCount == null) this.reportCount = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete - sets status to DELETED and records deletion time.
     */
    public void softDelete() {
        this.status = PostStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount = this.likeCount == null ? 1L : this.likeCount + 1;
    }

    public void decrementLikeCount() {
        this.likeCount = (this.likeCount == null || this.likeCount <= 0) ? 0L : this.likeCount - 1;
    }

    public void incrementCommentCount() {
        this.commentCount = this.commentCount == null ? 1L : this.commentCount + 1;
    }

    public void decrementCommentCount() {
        this.commentCount = (this.commentCount == null || this.commentCount <= 0) ? 0L : this.commentCount - 1;
    }

    public void incrementSaveCount() {
        this.saveCount = this.saveCount == null ? 1L : this.saveCount + 1;
    }

    public void decrementSaveCount() {
        this.saveCount = (this.saveCount == null || this.saveCount <= 0) ? 0L : this.saveCount - 1;
    }

    public void incrementReportCount() {
        this.reportCount = this.reportCount == null ? 1L : this.reportCount + 1;
    }
}

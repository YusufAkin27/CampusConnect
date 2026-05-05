package post_service.entity;

import jakarta.persistence.*;
import lombok.*;
import post_service.enums.CommentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"post", "parentComment", "replies", "commentLikes", "commentReports"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommentStatus status;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "reply_count")
    private Long replyCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "edited")
    private Boolean edited;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentReport> commentReports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = CommentStatus.ACTIVE;
        if (this.likeCount == null) this.likeCount = 0L;
        if (this.replyCount == null) this.replyCount = 0L;
        if (this.reportCount == null) this.reportCount = 0L;
        if (this.edited == null) this.edited = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete - sets status DELETED, replaces content, records deletion time.
     */
    public void softDelete() {
        this.status = CommentStatus.DELETED;
        this.content = "Bu yorum silindi.";
        this.deletedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount = this.likeCount == null ? 1L : this.likeCount + 1;
    }

    public void decrementLikeCount() {
        this.likeCount = (this.likeCount == null || this.likeCount <= 0) ? 0L : this.likeCount - 1;
    }

    public void incrementReplyCount() {
        this.replyCount = this.replyCount == null ? 1L : this.replyCount + 1;
    }

    public void decrementReplyCount() {
        this.replyCount = (this.replyCount == null || this.replyCount <= 0) ? 0L : this.replyCount - 1;
    }

    public void incrementReportCount() {
        this.reportCount = this.reportCount == null ? 1L : this.reportCount + 1;
    }
}

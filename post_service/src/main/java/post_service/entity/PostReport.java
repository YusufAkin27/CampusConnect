package post_service.entity;

import jakarta.persistence.*;
import lombok.*;
import post_service.enums.ReportReason;
import post_service.enums.ReportStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reports",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_report_user",
                columnNames = {"post_id", "reporter_auth_user_id"}
        ))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "post")
public class PostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "reporter_auth_user_id", nullable = false)
    private Long reporterAuthUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = ReportStatus.PENDING;
    }
}

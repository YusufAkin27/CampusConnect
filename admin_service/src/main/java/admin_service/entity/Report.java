package admin_service.entity;

import admin_service.enums.ReportReason;
import admin_service.enums.ReportStatus;
import admin_service.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Report / complaint entity for tracking user-submitted reports
 * about users, posts, comments, media, or messages.
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_report_status", columnList = "status"),
        @Index(name = "idx_report_target", columnList = "targetType,targetId"),
        @Index(name = "idx_report_reporter", columnList = "reporterUserId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporterUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportReason reason;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    private Long reviewedByAdminId;

    private LocalDateTime reviewedAt;

    @Column(length = 1000)
    private String resolutionNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package admin_service.entity;

import admin_service.enums.TicketPriority;
import admin_service.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Support ticket entity for user help requests
 * managed by admin/support staff.
 */
@Entity
@Table(name = "support_tickets", indexes = {
        @Index(name = "idx_ticket_user_id", columnList = "userId"),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_assigned", columnList = "assignedAdminId"),
        @Index(name = "idx_ticket_priority", columnList = "priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;

    private Long assignedAdminId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SupportTicketReply> replies = new ArrayList<>();

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

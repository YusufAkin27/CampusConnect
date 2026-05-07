package admin_service.entity;

import admin_service.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Reply to a support ticket, can be from either a user or an admin.
 */
@Entity
@Table(name = "support_ticket_replies", indexes = {
        @Index(name = "idx_reply_ticket_id", columnList = "ticket_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SenderType senderType;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

package admin_service.dto.response;

import admin_service.enums.TicketPriority;
import admin_service.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponse {
    private Long id;
    private Long userId;
    private String subject;
    private String message;
    private TicketStatus status;
    private TicketPriority priority;
    private Long assignedAdminId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}

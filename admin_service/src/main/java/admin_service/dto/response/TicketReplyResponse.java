package admin_service.dto.response;

import admin_service.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReplyResponse {
    private Long id;
    private Long ticketId;
    private SenderType senderType;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;
}

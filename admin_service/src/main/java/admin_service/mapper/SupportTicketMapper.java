package admin_service.mapper;

import admin_service.dto.response.SupportTicketDetailResponse;
import admin_service.dto.response.SupportTicketResponse;
import admin_service.dto.response.TicketReplyResponse;
import admin_service.entity.SupportTicket;
import admin_service.entity.SupportTicketReply;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupportTicketMapper {

    public SupportTicketResponse toResponse(SupportTicket entity) {
        if (entity == null) return null;
        return SupportTicketResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .assignedAdminId(entity.getAssignedAdminId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .closedAt(entity.getClosedAt())
                .build();
    }

    public SupportTicketDetailResponse toDetailResponse(SupportTicket entity) {
        if (entity == null) return null;

        List<TicketReplyResponse> replies = entity.getReplies() == null
                ? List.of()
                : entity.getReplies().stream()
                    .map(this::toReplyResponse)
                    .collect(Collectors.toList());

        return SupportTicketDetailResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .assignedAdminId(entity.getAssignedAdminId())
                .replies(replies)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .closedAt(entity.getClosedAt())
                .build();
    }

    public TicketReplyResponse toReplyResponse(SupportTicketReply entity) {
        if (entity == null) return null;
        return TicketReplyResponse.builder()
                .id(entity.getId())
                .ticketId(entity.getTicket().getId())
                .senderType(entity.getSenderType())
                .senderId(entity.getSenderId())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

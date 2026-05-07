package admin_service.service;

import admin_service.dto.request.*;
import admin_service.dto.response.SupportTicketDetailResponse;
import admin_service.dto.response.SupportTicketResponse;
import admin_service.dto.response.TicketReplyResponse;
import admin_service.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportTicketService {

    SupportTicketResponse createTicket(CreateTicketRequest request);

    SupportTicketDetailResponse getTicketById(Long ticketId);

    Page<SupportTicketResponse> getAllTickets(Pageable pageable);

    Page<SupportTicketResponse> getTicketsByStatus(TicketStatus status, Pageable pageable);

    SupportTicketResponse assignTicket(Long ticketId, AssignTicketRequest request);

    SupportTicketResponse updateTicketStatus(Long ticketId, UpdateTicketStatusRequest request);

    TicketReplyResponse replyToTicket(Long ticketId, TicketReplyRequest request, Long adminId);

    void deleteTicket(Long ticketId);
}

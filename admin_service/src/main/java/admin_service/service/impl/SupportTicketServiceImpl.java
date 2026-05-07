package admin_service.service.impl;

import admin_service.dto.request.*;
import admin_service.dto.response.SupportTicketDetailResponse;
import admin_service.dto.response.SupportTicketResponse;
import admin_service.dto.response.TicketReplyResponse;
import admin_service.entity.SupportTicket;
import admin_service.entity.SupportTicketReply;
import admin_service.enums.*;
import admin_service.exception.SupportTicketNotFoundException;
import admin_service.mapper.SupportTicketMapper;
import admin_service.repository.SupportTicketRepository;
import admin_service.security.AdminAction;
import admin_service.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMapper ticketMapper;

    @Override
    public SupportTicketResponse createTicket(CreateTicketRequest request) {
        SupportTicket ticket = SupportTicket.builder()
                .userId(request.getUserId())
                .subject(request.getSubject())
                .message(request.getMessage())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .build();
        SupportTicket saved = ticketRepository.save(ticket);
        log.info("Support ticket created: {} for user {}", saved.getId(), saved.getUserId());
        return ticketMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicketDetailResponse getTicketById(Long ticketId) {
        return ticketMapper.toDetailResponse(findTicketOrThrow(ticketId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(ticketMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getTicketsByStatus(TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable).map(ticketMapper::toResponse);
    }

    @Override
    @AdminAction(actionType = ActionType.TICKET_ASSIGNED, targetType = TargetType.USER)
    public SupportTicketResponse assignTicket(Long ticketId, AssignTicketRequest request) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        ticket.setAssignedAdminId(request.getAdminId());
        if (ticket.getStatus() == TicketStatus.OPEN) ticket.setStatus(TicketStatus.IN_PROGRESS);
        SupportTicket updated = ticketRepository.save(ticket);
        log.info("Ticket {} assigned to admin {}", ticketId, request.getAdminId());
        return ticketMapper.toResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.TICKET_STATUS_CHANGED, targetType = TargetType.USER)
    public SupportTicketResponse updateTicketStatus(Long ticketId, UpdateTicketStatusRequest request) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        ticket.setStatus(request.getStatus());
        if (request.getStatus() == TicketStatus.CLOSED || request.getStatus() == TicketStatus.REJECTED) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        SupportTicket updated = ticketRepository.save(ticket);
        log.info("Ticket {} status changed to {}", ticketId, request.getStatus());
        return ticketMapper.toResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.TICKET_REPLIED, targetType = TargetType.USER)
    public TicketReplyResponse replyToTicket(Long ticketId, TicketReplyRequest request, Long adminId) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        SupportTicketReply reply = SupportTicketReply.builder()
                .ticket(ticket)
                .senderType(SenderType.ADMIN)
                .senderId(adminId)
                .message(request.getMessage())
                .build();
        ticket.getReplies().add(reply);
        if (ticket.getStatus() == TicketStatus.OPEN) ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepository.save(ticket);
        log.info("Admin {} replied to ticket {}", adminId, ticketId);
        return ticketMapper.toReplyResponse(reply);
    }

    @Override
    public void deleteTicket(Long ticketId) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        ticketRepository.delete(ticket);
        log.info("Ticket {} deleted", ticketId);
    }

    private SupportTicket findTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new SupportTicketNotFoundException("Ticket not found: " + ticketId));
    }
}

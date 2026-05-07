package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.common.response.PagedResponse;
import admin_service.common.response.ResponseMessage;
import admin_service.dto.request.*;
import admin_service.dto.response.SupportTicketDetailResponse;
import admin_service.dto.response.SupportTicketResponse;
import admin_service.dto.response.TicketReplyResponse;
import admin_service.enums.TicketStatus;
import admin_service.security.AdminAuthProvider;
import admin_service.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/support/tickets")
@RequiredArgsConstructor
@Tag(name = "Support Ticket Management", description = "Manage user support tickets")
public class SupportTicketController {

    private final SupportTicketService ticketService;
    private final AdminAuthProvider adminAuthProvider;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Create a support ticket")
    public ResponseEntity<DataResponseMessage<SupportTicketResponse>> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponseMessage.success("Ticket created.", ticketService.createTicket(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "List all tickets")
    public ResponseEntity<DataResponseMessage<PagedResponse<SupportTicketResponse>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<SupportTicketResponse> result = ticketService.getAllTickets(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Tickets.", toPagedResponse(result)));
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Get ticket details with replies")
    public ResponseEntity<DataResponseMessage<SupportTicketDetailResponse>> getById(@PathVariable Long ticketId) {
        return ResponseEntity.ok(DataResponseMessage.success("Ticket details.", ticketService.getTicketById(ticketId)));
    }

    @PatchMapping("/{ticketId}/assign")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Operation(summary = "Assign ticket to admin")
    public ResponseEntity<DataResponseMessage<SupportTicketResponse>> assign(@PathVariable Long ticketId, @Valid @RequestBody AssignTicketRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Ticket assigned.", ticketService.assignTicket(ticketId, request)));
    }

    @PatchMapping("/{ticketId}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Operation(summary = "Update ticket status")
    public ResponseEntity<DataResponseMessage<SupportTicketResponse>> updateStatus(@PathVariable Long ticketId, @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Status updated.", ticketService.updateTicketStatus(ticketId, request)));
    }

    @PostMapping("/{ticketId}/reply")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Reply to a ticket")
    public ResponseEntity<DataResponseMessage<TicketReplyResponse>> reply(@PathVariable Long ticketId, @Valid @RequestBody TicketReplyRequest request) {
        Long adminId = adminAuthProvider.getCurrentAdminId();
        return ResponseEntity.ok(DataResponseMessage.success("Reply sent.", ticketService.replyToTicket(ticketId, request, adminId)));
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Operation(summary = "Delete a ticket")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.ok(ResponseMessage.success("Ticket deleted."));
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> p) {
        return PagedResponse.of(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast());
    }
}

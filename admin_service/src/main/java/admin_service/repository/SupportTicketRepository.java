package admin_service.repository;

import admin_service.entity.SupportTicket;
import admin_service.enums.TicketPriority;
import admin_service.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    Page<SupportTicket> findByUserId(Long userId, Pageable pageable);

    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);

    Page<SupportTicket> findByAssignedAdminId(Long adminId, Pageable pageable);

    Page<SupportTicket> findByPriority(TicketPriority priority, Pageable pageable);

    long countByStatus(TicketStatus status);

    Page<SupportTicket> findByStatusAndPriority(TicketStatus status, TicketPriority priority, Pageable pageable);
}

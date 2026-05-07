package admin_service.repository;

import admin_service.entity.SupportTicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketReplyRepository extends JpaRepository<SupportTicketReply, Long> {

    List<SupportTicketReply> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}

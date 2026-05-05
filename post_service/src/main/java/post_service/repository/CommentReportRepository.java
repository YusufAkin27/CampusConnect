package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.CommentReport;

import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    boolean existsByCommentIdAndReporterAuthUserId(Long commentId, Long reporterAuthUserId);

    Page<CommentReport> findAll(Pageable pageable);

    Optional<CommentReport> findById(Long id);
}

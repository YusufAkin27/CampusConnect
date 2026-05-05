package post_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import post_service.entity.PostReport;

import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    boolean existsByPostIdAndReporterAuthUserId(Long postId, Long reporterAuthUserId);

    Page<PostReport> findAll(Pageable pageable);

    Optional<PostReport> findById(Long id);
}

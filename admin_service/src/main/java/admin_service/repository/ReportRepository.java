package admin_service.repository;

import admin_service.entity.Report;
import admin_service.enums.ReportStatus;
import admin_service.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    Page<Report> findByTargetTypeAndTargetId(TargetType targetType, Long targetId, Pageable pageable);

    Page<Report> findByReporterUserId(Long reporterUserId, Pageable pageable);

    Page<Report> findByReviewedByAdminId(Long adminId, Pageable pageable);

    long countByStatus(ReportStatus status);

    Page<Report> findByTargetType(TargetType targetType, Pageable pageable);
}

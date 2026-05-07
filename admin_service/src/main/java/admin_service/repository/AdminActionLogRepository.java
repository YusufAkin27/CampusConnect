package admin_service.repository;

import admin_service.entity.AdminActionLog;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    Page<AdminActionLog> findByAdminId(Long adminId, Pageable pageable);

    Page<AdminActionLog> findByTargetTypeAndTargetId(TargetType targetType, Long targetId, Pageable pageable);

    Page<AdminActionLog> findByActionType(ActionType actionType, Pageable pageable);

    Page<AdminActionLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM AdminActionLog a WHERE " +
            "(:adminId IS NULL OR a.adminId = :adminId) AND " +
            "(:actionType IS NULL OR a.actionType = :actionType) AND " +
            "(:targetType IS NULL OR a.targetType = :targetType) AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt <= :endDate)")
    Page<AdminActionLog> findByFilters(
            @Param("adminId") Long adminId,
            @Param("actionType") ActionType actionType,
            @Param("targetType") TargetType targetType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}

package admin_service.repository;

import admin_service.entity.UserBanRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBanRecordRepository extends JpaRepository<UserBanRecord, Long> {

    List<UserBanRecord> findByUserId(Long userId);

    Optional<UserBanRecord> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndActiveTrue(Long userId);

    Page<UserBanRecord> findByBannedByAdminId(Long adminId, Pageable pageable);

    Page<UserBanRecord> findByActiveTrue(Pageable pageable);

    List<UserBanRecord> findByActiveTrueAndExpiresAtIsNotNull();
}

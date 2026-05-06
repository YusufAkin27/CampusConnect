package logging_service.repository;

import logging_service.entity.LogRetentionPolicy;
import logging_service.enums.LogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogRetentionPolicyRepository extends JpaRepository<LogRetentionPolicy, Long> {

    Optional<LogRetentionPolicy> findByCategory(LogCategory category);

    List<LogRetentionPolicy> findByEnabledTrue();

    boolean existsByCategory(LogCategory category);
}

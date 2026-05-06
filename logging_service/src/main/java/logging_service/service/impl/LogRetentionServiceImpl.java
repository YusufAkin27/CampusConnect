package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.ResponseMessage;
import logging_service.dto.request.UpdateRetentionPolicyRequest;
import logging_service.dto.response.RetentionPolicyResponse;
import logging_service.entity.LogRetentionPolicy;
import logging_service.enums.LogCategory;
import logging_service.mapper.RetentionPolicyMapper;
import logging_service.repository.ApiRequestLogRepository;
import logging_service.repository.AuditLogRepository;
import logging_service.repository.ErrorLogRepository;
import logging_service.repository.LogEntryRepository;
import logging_service.repository.LogRetentionPolicyRepository;
import logging_service.repository.SecurityLogRepository;
import logging_service.service.LogRetentionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing log retention policies and performing cleanup operations.
 *
 * TODO: For production scale, consider PostgreSQL table partitioning (PARTITION BY RANGE on created_at)
 * to enable efficient partition-based DROP instead of row-by-row DELETE operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogRetentionServiceImpl implements LogRetentionService {

    private final LogRetentionPolicyRepository retentionPolicyRepository;
    private final LogEntryRepository logEntryRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final ErrorLogRepository errorLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final RetentionPolicyMapper retentionPolicyMapper;

    @Override
    @Transactional
    public DataResponseMessage<RetentionPolicyResponse> createOrUpdatePolicy(UpdateRetentionPolicyRequest request) {
        LogRetentionPolicy policy = retentionPolicyRepository.findByCategory(request.getCategory())
                .orElse(LogRetentionPolicy.builder().category(request.getCategory()).build());

        policy.setRetentionDays(request.getRetentionDays());
        policy.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        LogRetentionPolicy saved = retentionPolicyRepository.save(policy);
        return DataResponseMessage.success("Retention policy saved", retentionPolicyMapper.toRetentionPolicyResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<List<RetentionPolicyResponse>> getPolicies() {
        List<RetentionPolicyResponse> policies = retentionPolicyRepository.findAll()
                .stream()
                .map(retentionPolicyMapper::toRetentionPolicyResponse)
                .collect(Collectors.toList());
        return DataResponseMessage.success("Retention policies", policies);
    }

    @Override
    @Transactional
    public ResponseMessage cleanupOldLogs() {
        log.info("Starting full log cleanup based on retention policies...");
        List<LogRetentionPolicy> enabledPolicies = retentionPolicyRepository.findByEnabledTrue();

        if (enabledPolicies.isEmpty()) {
            log.warn("No enabled retention policies found. Skipping cleanup.");
            return ResponseMessage.success("No enabled retention policies. Cleanup skipped.");
        }

        int totalDeleted = 0;
        for (LogRetentionPolicy policy : enabledPolicies) {
            try {
                int deleted = cleanupByCategory(policy.getCategory(), policy.getRetentionDays());
                totalDeleted += deleted;
                log.info("Cleaned up {} logs for category {} (retention: {} days)",
                        deleted, policy.getCategory(), policy.getRetentionDays());
            } catch (Exception e) {
                log.error("Failed to cleanup logs for category {}: {}", policy.getCategory(), e.getMessage());
            }
        }

        return ResponseMessage.success("Log cleanup completed. Categories processed: " + enabledPolicies.size());
    }

    @Override
    @Transactional
    public ResponseMessage cleanupLogsByCategory(LogCategory category) {
        log.info("Starting log cleanup for category: {}", category);

        LogRetentionPolicy policy = retentionPolicyRepository.findByCategory(category)
                .orElse(null);

        int retentionDays = policy != null ? policy.getRetentionDays() : 90;

        try {
            int deleted = cleanupByCategory(category, retentionDays);
            return ResponseMessage.success("Cleanup completed for category " + category + ". Deleted approximately " + deleted + " records.");
        } catch (Exception e) {
            log.error("Failed to cleanup logs for category {}: {}", category, e.getMessage());
            return ResponseMessage.failure("Cleanup failed for category " + category + ": " + e.getMessage());
        }
    }

    /**
     * Performs the actual deletion for a given category and retention period.
     * Returns approximate deleted count (not always exact due to different table deletions).
     */
    private int cleanupByCategory(LogCategory category, int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);

        return switch (category) {
            case API_REQUEST -> {
                apiRequestLogRepository.deleteByCreatedAtBefore(cutoffDate);
                // Also delete from log_entries for API_REQUEST category
                logEntryRepository.deleteByCreatedAtBefore(cutoffDate);
                yield 1; // Approximate
            }
            case ERROR -> {
                errorLogRepository.deleteByCreatedAtBefore(cutoffDate);
                logEntryRepository.deleteByCreatedAtBefore(cutoffDate);
                yield 1;
            }
            case SECURITY -> {
                securityLogRepository.deleteByCreatedAtBefore(cutoffDate);
                logEntryRepository.deleteByCreatedAtBefore(cutoffDate);
                yield 1;
            }
            case AUDIT -> {
                auditLogRepository.deleteByCreatedAtBefore(cutoffDate);
                yield 1;
            }
            default -> {
                // For APPLICATION, SYSTEM, BUSINESS, AUTH, DATABASE, EXTERNAL_SERVICE
                // Only clean up log_entries
                logEntryRepository.deleteByCreatedAtBefore(cutoffDate);
                yield 1;
            }
        };
    }
}

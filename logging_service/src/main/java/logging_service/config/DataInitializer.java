package logging_service.config;

import logging_service.entity.LogRetentionPolicy;
import logging_service.entity.LogEntry;
import logging_service.entity.ApiRequestLog;
import logging_service.entity.ErrorLog;
import logging_service.entity.AuditLog;
import logging_service.entity.SecurityLog;
import logging_service.enums.*;
import logging_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DataInitializer for seeding test data in development.
 * Controlled by app.seed.enabled property.
 * Does NOT run in production when app.seed.enabled=false.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final LogEntryRepository logEntryRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;
    private final ErrorLogRepository errorLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final LogRetentionPolicyRepository retentionPolicyRepository;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (!seedEnabled) {
                log.info("DataInitializer: seed is disabled (app.seed.enabled=false). Skipping.");
                initDefaultRetentionPolicies();
                return;
            }

            log.info("DataInitializer: seeding example data...");
            seedRetentionPolicies();
            seedExampleLogs();
            log.info("DataInitializer: seed complete.");
        };
    }

    /**
     * Always initialize default retention policies if they don't exist.
     */
    private void initDefaultRetentionPolicies() {
        createPolicyIfMissing(LogCategory.API_REQUEST, 30);
        createPolicyIfMissing(LogCategory.APPLICATION, 60);
        createPolicyIfMissing(LogCategory.ERROR, 180);
        createPolicyIfMissing(LogCategory.SECURITY, 365);
        createPolicyIfMissing(LogCategory.AUDIT, 730);
        createPolicyIfMissing(LogCategory.BUSINESS, 180);
        createPolicyIfMissing(LogCategory.SYSTEM, 90);
        createPolicyIfMissing(LogCategory.AUTH, 365);
        createPolicyIfMissing(LogCategory.DATABASE, 90);
        createPolicyIfMissing(LogCategory.EXTERNAL_SERVICE, 30);
        log.info("Default retention policies initialized.");
    }

    private void seedRetentionPolicies() {
        initDefaultRetentionPolicies();
    }

    private void seedExampleLogs() {
        if (logEntryRepository.count() > 0) {
            log.info("Log entries already exist. Skipping log seed.");
            return;
        }

        // Example application log
        LogEntry appLog = LogEntry.builder()
                .traceId("trace-001")
                .correlationId("corr-001")
                .requestId("req-001")
                .serviceName("auth-service")
                .level(LogLevel.INFO)
                .category(LogCategory.APPLICATION)
                .message("User authentication service started successfully")
                .environment("DEV")
                .hostName("localhost")
                .build();
        logEntryRepository.save(appLog);

        // Example error log
        ErrorLog errorLog = ErrorLog.builder()
                .traceId("trace-002")
                .serviceName("user-service")
                .exceptionClass("java.lang.NullPointerException")
                .exceptionMessage("User not found during profile fetch")
                .severity(ErrorSeverity.HIGH)
                .resolved(false)
                .environment("DEV")
                .build();
        errorLogRepository.save(errorLog);

        // Example audit log
        AuditLog auditLog = AuditLog.builder()
                .traceId("trace-003")
                .authUserId(1L)
                .username("john.doe")
                .serviceName("user-service")
                .action(AuditAction.PROFILE_UPDATE)
                .targetType("USER")
                .targetId("1")
                .description("User updated their profile")
                .build();
        auditLogRepository.save(auditLog);

        // Example API request log
        ApiRequestLog apiLog = ApiRequestLog.builder()
                .traceId("trace-004")
                .serviceName("auth-service")
                .httpMethod("POST")
                .endpoint("/v1/api/auth/login")
                .httpStatus(200)
                .durationMs(145L)
                .success(true)
                .clientIp("192.168.1.1")
                .build();
        apiRequestLogRepository.save(apiLog);

        // Example security log
        SecurityLog securityLog = SecurityLog.builder()
                .traceId("trace-005")
                .serviceName("auth-service")
                .eventType(SecurityEventType.LOGIN_FAILURE)
                .severity(SecuritySeverity.MEDIUM)
                .clientIp("192.168.1.100")
                .message("Failed login attempt for user: unknown@test.com")
                .build();
        securityLogRepository.save(securityLog);

        log.info("Example seed data created successfully.");
    }

    private void createPolicyIfMissing(LogCategory category, int retentionDays) {
        if (!retentionPolicyRepository.existsByCategory(category)) {
            LogRetentionPolicy policy = LogRetentionPolicy.builder()
                    .category(category)
                    .retentionDays(retentionDays)
                    .enabled(true)
                    .build();
            retentionPolicyRepository.save(policy);
        }
    }
}

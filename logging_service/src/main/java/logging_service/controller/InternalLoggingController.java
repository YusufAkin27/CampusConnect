package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.dto.request.BatchLogEntryRequest;
import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.dto.request.CreateAuditLogRequest;
import logging_service.dto.request.CreateErrorLogRequest;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.dto.request.CreateSecurityLogRequest;
import logging_service.dto.response.ApiRequestLogResponse;
import logging_service.dto.response.AuditLogResponse;
import logging_service.dto.response.BatchLogResponse;
import logging_service.dto.response.ErrorLogResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.dto.response.SecurityLogResponse;
import logging_service.service.ApiRequestLogService;
import logging_service.service.AuditLogService;
import logging_service.service.ErrorLogService;
import logging_service.service.LogIngestionService;
import logging_service.service.SecurityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal endpoints used by other CampusConnect microservices to send logs.
 *
 * TODO: In production, these endpoints should be protected with service-to-service authentication
 * (e.g., mutual TLS, internal JWT, or API key validation).
 *
 * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
 * These internal endpoints are for the initial HTTP-based ingestion phase only.
 */
@RestController
@RequestMapping("/v1/api/logs/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Log Ingestion", description = "Internal endpoints for microservices to send logs")
public class InternalLoggingController {

    private final LogIngestionService logIngestionService;
    private final AuditLogService auditLogService;
    private final ApiRequestLogService apiRequestLogService;
    private final ErrorLogService errorLogService;
    private final SecurityLogService securityLogService;

    @PostMapping("/application")
    @Operation(summary = "Receive application log from another microservice")
    public ResponseEntity<DataResponseMessage<LogEntryResponse>> receiveApplicationLog(
            @Valid @RequestBody CreateLogEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logIngestionService.createLog(request));
    }

    @PostMapping("/api-request")
    @Operation(summary = "Receive API request log from another microservice")
    public ResponseEntity<DataResponseMessage<ApiRequestLogResponse>> receiveApiRequestLog(
            @Valid @RequestBody CreateApiRequestLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiRequestLogService.createApiRequestLog(request));
    }

    @PostMapping("/error")
    @Operation(summary = "Receive error log from another microservice")
    public ResponseEntity<DataResponseMessage<ErrorLogResponse>> receiveErrorLog(
            @Valid @RequestBody CreateErrorLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(errorLogService.createErrorLog(request));
    }

    @PostMapping("/security")
    @Operation(summary = "Receive security log from another microservice")
    public ResponseEntity<DataResponseMessage<SecurityLogResponse>> receiveSecurityLog(
            @Valid @RequestBody CreateSecurityLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securityLogService.createSecurityLog(request));
    }

    @PostMapping("/audit")
    @Operation(summary = "Receive audit log from another microservice")
    public ResponseEntity<DataResponseMessage<AuditLogResponse>> receiveAuditLog(
            @Valid @RequestBody CreateAuditLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditLogService.createAuditLog(request));
    }

    @PostMapping("/batch")
    @Operation(summary = "Receive batch of logs from another microservice")
    public ResponseEntity<DataResponseMessage<BatchLogResponse>> receiveBatchLogs(
            @Valid @RequestBody BatchLogEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logIngestionService.createBatchLogs(request));
    }
}

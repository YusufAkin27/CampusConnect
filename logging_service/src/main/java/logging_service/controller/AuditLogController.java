package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateAuditLogRequest;
import logging_service.dto.response.AuditLogResponse;
import logging_service.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logs/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Endpoints for audit trail management")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping
    @Operation(summary = "Create an audit log entry")
    public ResponseEntity<DataResponseMessage<AuditLogResponse>> createAuditLog(
            @Valid @RequestBody CreateAuditLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditLogService.createAuditLog(request));
    }

    @GetMapping("/user/{authUserId}")
    @Operation(summary = "Get audit logs for a user")
    public ResponseEntity<DataResponseMessage<PageResponse<AuditLogResponse>>> getAuditLogsByUser(
            @PathVariable Long authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUser(authUserId, page, size));
    }

    @GetMapping("/target")
    @Operation(summary = "Get audit logs for a specific target entity")
    public ResponseEntity<DataResponseMessage<PageResponse<AuditLogResponse>>> getAuditLogsByTarget(
            @RequestParam String targetType,
            @RequestParam String targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByTarget(targetType, targetId, page, size));
    }
}

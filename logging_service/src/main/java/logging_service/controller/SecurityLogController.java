package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateSecurityLogRequest;
import logging_service.dto.response.SecurityLogResponse;
import logging_service.service.SecurityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logs/security")
@RequiredArgsConstructor
@Tag(name = "Security Logs", description = "Endpoints for security event log management")
public class SecurityLogController {

    private final SecurityLogService securityLogService;

    @PostMapping
    @Operation(summary = "Create a security log entry")
    public ResponseEntity<DataResponseMessage<SecurityLogResponse>> createSecurityLog(
            @Valid @RequestBody CreateSecurityLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securityLogService.createSecurityLog(request));
    }

    @GetMapping("/user/{authUserId}")
    @Operation(summary = "Get security logs for a user")
    public ResponseEntity<DataResponseMessage<PageResponse<SecurityLogResponse>>> getSecurityLogsByUser(
            @PathVariable Long authUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(securityLogService.getSecurityLogsByUser(authUserId, page, size));
    }

    @GetMapping("/ip/{clientIp}")
    @Operation(summary = "Get security logs for a specific IP address")
    public ResponseEntity<DataResponseMessage<PageResponse<SecurityLogResponse>>> getSecurityLogsByIp(
            @PathVariable String clientIp,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(securityLogService.getSecurityLogsByIp(clientIp, page, size));
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical severity security logs")
    public ResponseEntity<DataResponseMessage<PageResponse<SecurityLogResponse>>> getCriticalSecurityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(securityLogService.getCriticalSecurityLogs(page, size));
    }
}

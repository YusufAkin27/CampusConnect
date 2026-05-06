package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateErrorLogRequest;
import logging_service.dto.request.ResolveErrorLogRequest;
import logging_service.dto.response.ErrorLogResponse;
import logging_service.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logs/errors")
@RequiredArgsConstructor
@Tag(name = "Error Logs", description = "Endpoints for error and exception log management")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @PostMapping
    @Operation(summary = "Create an error log entry")
    public ResponseEntity<DataResponseMessage<ErrorLogResponse>> createErrorLog(
            @Valid @RequestBody CreateErrorLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(errorLogService.createErrorLog(request));
    }

    @GetMapping("/{errorLogId}")
    @Operation(summary = "Get error log by ID")
    public ResponseEntity<DataResponseMessage<ErrorLogResponse>> getErrorLogById(
            @PathVariable Long errorLogId) {
        return ResponseEntity.ok(errorLogService.getErrorLogById(errorLogId));
    }

    @GetMapping("/unresolved")
    @Operation(summary = "Get all unresolved error logs")
    public ResponseEntity<DataResponseMessage<PageResponse<ErrorLogResponse>>> getUnresolvedErrors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(errorLogService.getUnresolvedErrors(page, size));
    }

    @GetMapping("/critical")
    @Operation(summary = "Get all critical unresolved error logs")
    public ResponseEntity<DataResponseMessage<PageResponse<ErrorLogResponse>>> getCriticalErrors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(errorLogService.getCriticalErrors(page, size));
    }

    @PatchMapping("/{errorLogId}/resolve")
    @Operation(summary = "Mark an error log as resolved")
    public ResponseEntity<DataResponseMessage<ErrorLogResponse>> resolveErrorLog(
            @PathVariable Long errorLogId,
            @Valid @RequestBody ResolveErrorLogRequest request) {
        return ResponseEntity.ok(errorLogService.resolveErrorLog(errorLogId, request));
    }
}

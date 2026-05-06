package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.ResponseMessage;
import logging_service.dto.request.UpdateRetentionPolicyRequest;
import logging_service.dto.response.RetentionPolicyResponse;
import logging_service.enums.LogCategory;
import logging_service.service.LogRetentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/logs/retention")
@RequiredArgsConstructor
@Tag(name = "Log Retention", description = "Endpoints for managing log retention policies and cleanup")
public class LogRetentionController {

    private final LogRetentionService logRetentionService;

    @GetMapping("/policies")
    @Operation(summary = "Get all retention policies")
    public ResponseEntity<DataResponseMessage<List<RetentionPolicyResponse>>> getPolicies() {
        return ResponseEntity.ok(logRetentionService.getPolicies());
    }

    @PostMapping("/policies")
    @Operation(summary = "Create or update a retention policy")
    public ResponseEntity<DataResponseMessage<RetentionPolicyResponse>> createOrUpdatePolicy(
            @Valid @RequestBody UpdateRetentionPolicyRequest request) {
        return ResponseEntity.ok(logRetentionService.createOrUpdatePolicy(request));
    }

    @PostMapping("/cleanup")
    @Operation(summary = "Trigger manual cleanup of all log categories")
    public ResponseEntity<ResponseMessage> cleanup() {
        return ResponseEntity.ok(logRetentionService.cleanupOldLogs());
    }

    @PostMapping("/cleanup/{category}")
    @Operation(summary = "Trigger cleanup for a specific log category")
    public ResponseEntity<ResponseMessage> cleanupByCategory(
            @PathVariable LogCategory category) {
        return ResponseEntity.ok(logRetentionService.cleanupLogsByCategory(category));
    }
}

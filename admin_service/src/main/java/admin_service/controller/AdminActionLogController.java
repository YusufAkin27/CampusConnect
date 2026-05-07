package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.common.response.PagedResponse;
import admin_service.dto.request.ActionLogFilterRequest;
import admin_service.dto.response.AdminActionLogResponse;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import admin_service.service.AdminActionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/admin/action-logs")
@RequiredArgsConstructor
@Tag(name = "Admin Action Logs", description = "Query audit logs of admin actions")
public class AdminActionLogController {

    private final AdminActionLogService logService;

    @GetMapping
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "List all action logs")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminActionLogResponse>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminActionLogResponse> result = logService.getAllLogs(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Action logs.", toPagedResponse(result)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "Get action log details")
    public ResponseEntity<DataResponseMessage<AdminActionLogResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(DataResponseMessage.success("Action log.", logService.getLogById(id)));
    }

    @GetMapping("/by-admin/{adminId}")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "Get action logs by admin")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminActionLogResponse>>> getByAdmin(@PathVariable Long adminId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminActionLogResponse> result = logService.getLogsByAdminId(adminId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Admin action logs.", toPagedResponse(result)));
    }

    @GetMapping("/by-target")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "Get action logs by target")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminActionLogResponse>>> getByTarget(@RequestParam TargetType targetType, @RequestParam Long targetId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminActionLogResponse> result = logService.getLogsByTarget(targetType, targetId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Target action logs.", toPagedResponse(result)));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "Filter action logs")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminActionLogResponse>>> filter(
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) ActionType actionType,
            @RequestParam(required = false) TargetType targetType,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ActionLogFilterRequest filter = ActionLogFilterRequest.builder()
                .adminId(adminId).actionType(actionType).targetType(targetType)
                .startDate(startDate).endDate(endDate).build();
        Page<AdminActionLogResponse> result = logService.filterLogs(filter, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Filtered logs.", toPagedResponse(result)));
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> p) {
        return PagedResponse.of(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast());
    }
}

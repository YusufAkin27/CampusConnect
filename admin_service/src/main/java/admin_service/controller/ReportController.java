package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.common.response.PagedResponse;
import admin_service.dto.request.*;
import admin_service.dto.response.ReportResponse;
import admin_service.enums.ReportStatus;
import admin_service.enums.TargetType;
import admin_service.security.AdminAuthProvider;
import admin_service.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/reports")
@RequiredArgsConstructor
@Tag(name = "Report Management", description = "Manage user reports and complaints")
public class ReportController {

    private final ReportService reportService;
    private final AdminAuthProvider adminAuthProvider;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Create a report")
    public ResponseEntity<DataResponseMessage<ReportResponse>> create(@Valid @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponseMessage.success("Report created.", reportService.createReport(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "List all reports")
    public ResponseEntity<DataResponseMessage<PagedResponse<ReportResponse>>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<ReportResponse> result = reportService.getAllReports(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Reports retrieved.", toPagedResponse(result)));
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get report details")
    public ResponseEntity<DataResponseMessage<ReportResponse>> getById(@PathVariable Long reportId) {
        return ResponseEntity.ok(DataResponseMessage.success("Report retrieved.", reportService.getReportById(reportId)));
    }

    @PatchMapping("/{reportId}/assign")
    @PreAuthorize("hasAuthority('REPORT_RESOLVE')")
    @Operation(summary = "Assign report to admin")
    public ResponseEntity<DataResponseMessage<ReportResponse>> assign(@PathVariable Long reportId, @Valid @RequestBody AssignReportRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Report assigned.", reportService.assignReport(reportId, request)));
    }

    @PatchMapping("/{reportId}/resolve")
    @PreAuthorize("hasAuthority('REPORT_RESOLVE')")
    @Operation(summary = "Resolve a report")
    public ResponseEntity<DataResponseMessage<ReportResponse>> resolve(@PathVariable Long reportId, @Valid @RequestBody ResolveReportRequest request) {
        Long adminId = adminAuthProvider.getCurrentAdminId();
        return ResponseEntity.ok(DataResponseMessage.success("Report resolved.", reportService.resolveReport(reportId, request, adminId)));
    }

    @PatchMapping("/{reportId}/reject")
    @PreAuthorize("hasAuthority('REPORT_RESOLVE')")
    @Operation(summary = "Reject a report")
    public ResponseEntity<DataResponseMessage<ReportResponse>> reject(@PathVariable Long reportId, @RequestParam String note) {
        Long adminId = adminAuthProvider.getCurrentAdminId();
        return ResponseEntity.ok(DataResponseMessage.success("Report rejected.", reportService.rejectReport(reportId, note, adminId)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    public ResponseEntity<DataResponseMessage<PagedResponse<ReportResponse>>> getByStatus(@PathVariable ReportStatus status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<ReportResponse> result = reportService.getReportsByStatus(status, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Reports by status.", toPagedResponse(result)));
    }

    @GetMapping("/target/{targetType}/{targetId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    public ResponseEntity<DataResponseMessage<PagedResponse<ReportResponse>>> getByTarget(@PathVariable TargetType targetType, @PathVariable Long targetId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<ReportResponse> result = reportService.getReportsByTarget(targetType, targetId, PageRequest.of(page, size));
        return ResponseEntity.ok(DataResponseMessage.success("Reports by target.", toPagedResponse(result)));
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> p) {
        return PagedResponse.of(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast());
    }
}

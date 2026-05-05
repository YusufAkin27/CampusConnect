package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.request.ReportCommentRequest;
import post_service.dto.request.ReportPostRequest;
import post_service.dto.request.UpdateReportStatusRequest;
import post_service.dto.response.ReportResponse;
import post_service.security.AuthUserProvider;
import post_service.service.ReportService;

@RestController
@RequestMapping("/v1/api/posts/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "Post and comment report endpoints")
public class ReportController {

    private final ReportService reportService;
    private final AuthUserProvider authUserProvider;

    @PostMapping("/post/{postId}")
    @Operation(summary = "Report post", description = "Reports a post for moderation.")
    public ResponseEntity<DataResponseMessage<ReportResponse>> reportPost(
            @PathVariable Long postId,
            @Valid @RequestBody ReportPostRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reportService.reportPost(authUserId, postId, request));
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "Report comment", description = "Reports a comment for moderation.")
    public ResponseEntity<DataResponseMessage<ReportResponse>> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody ReportCommentRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reportService.reportComment(authUserId, commentId, request));
    }

    @GetMapping("/admin/posts")
    @Operation(summary = "List post reports (Admin)", description = "Returns all post reports for admin review.")
    public ResponseEntity<DataResponseMessage<PageResponse<ReportResponse>>> getPostReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reportService.getPostReports(page, size));
    }

    @GetMapping("/admin/comments")
    @Operation(summary = "List comment reports (Admin)", description = "Returns all comment reports for admin review.")
    public ResponseEntity<DataResponseMessage<PageResponse<ReportResponse>>> getCommentReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reportService.getCommentReports(page, size));
    }

    @PatchMapping("/admin/posts/{reportId}")
    @Operation(summary = "Update post report status (Admin)", description = "Updates the status of a post report.")
    public ResponseEntity<DataResponseMessage<ReportResponse>> updatePostReportStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        return ResponseEntity.ok(reportService.updatePostReportStatus(reportId, request));
    }

    @PatchMapping("/admin/comments/{reportId}")
    @Operation(summary = "Update comment report status (Admin)", description = "Updates the status of a comment report.")
    public ResponseEntity<DataResponseMessage<ReportResponse>> updateCommentReportStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        return ResponseEntity.ok(reportService.updateCommentReportStatus(reportId, request));
    }
}

package post_service.service;

import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.request.ReportCommentRequest;
import post_service.dto.request.ReportPostRequest;
import post_service.dto.request.UpdateReportStatusRequest;
import post_service.dto.response.ReportResponse;

public interface ReportService {

    DataResponseMessage<ReportResponse> reportPost(Long authUserId, Long postId, ReportPostRequest request);

    DataResponseMessage<ReportResponse> reportComment(Long authUserId, Long commentId, ReportCommentRequest request);

    DataResponseMessage<PageResponse<ReportResponse>> getPostReports(int page, int size);

    DataResponseMessage<PageResponse<ReportResponse>> getCommentReports(int page, int size);

    DataResponseMessage<ReportResponse> updatePostReportStatus(Long reportId, UpdateReportStatusRequest request);

    DataResponseMessage<ReportResponse> updateCommentReportStatus(Long reportId, UpdateReportStatusRequest request);
}

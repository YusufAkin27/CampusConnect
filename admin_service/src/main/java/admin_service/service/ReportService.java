package admin_service.service;

import admin_service.dto.request.AssignReportRequest;
import admin_service.dto.request.CreateReportRequest;
import admin_service.dto.request.ResolveReportRequest;
import admin_service.dto.response.ReportResponse;
import admin_service.enums.ReportStatus;
import admin_service.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    ReportResponse createReport(CreateReportRequest request);

    ReportResponse getReportById(Long reportId);

    Page<ReportResponse> getAllReports(Pageable pageable);

    Page<ReportResponse> getReportsByStatus(ReportStatus status, Pageable pageable);

    Page<ReportResponse> getReportsByTarget(TargetType targetType, Long targetId, Pageable pageable);

    ReportResponse assignReport(Long reportId, AssignReportRequest request);

    ReportResponse resolveReport(Long reportId, ResolveReportRequest request, Long adminId);

    ReportResponse rejectReport(Long reportId, String resolutionNote, Long adminId);
}

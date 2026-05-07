package admin_service.service.impl;

import admin_service.dto.request.AssignReportRequest;
import admin_service.dto.request.CreateReportRequest;
import admin_service.dto.request.ResolveReportRequest;
import admin_service.dto.response.ReportResponse;
import admin_service.entity.Report;
import admin_service.enums.ActionType;
import admin_service.enums.ReportStatus;
import admin_service.enums.TargetType;
import admin_service.exception.InvalidReportStatusException;
import admin_service.exception.ReportNotFoundException;
import admin_service.mapper.ReportMapper;
import admin_service.repository.ReportRepository;
import admin_service.security.AdminAction;
import admin_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Override
    public ReportResponse createReport(CreateReportRequest request) {
        Report report = Report.builder()
                .reporterUserId(request.getReporterUserId())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        Report saved = reportRepository.save(report);
        log.info("Report created: {} targeting {} {}", saved.getId(), saved.getTargetType(), saved.getTargetId());
        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long reportId) {
        return reportMapper.toResponse(findReportOrThrow(reportId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable).map(reportMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> getReportsByTarget(TargetType targetType, Long targetId, Pageable pageable) {
        return reportRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable)
                .map(reportMapper::toResponse);
    }

    @Override
    @AdminAction(actionType = ActionType.REPORT_ASSIGNED, targetType = TargetType.USER)
    public ReportResponse assignReport(Long reportId, AssignReportRequest request) {
        Report report = findReportOrThrow(reportId);

        if (report.getStatus() == ReportStatus.RESOLVED || report.getStatus() == ReportStatus.REJECTED) {
            throw new InvalidReportStatusException("Cannot assign a report that is already " + report.getStatus());
        }

        report.setReviewedByAdminId(request.getAdminId());
        report.setStatus(ReportStatus.REVIEWING);
        Report updated = reportRepository.save(report);

        log.info("Report {} assigned to admin {}", reportId, request.getAdminId());
        return reportMapper.toResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.REPORT_RESOLVED, targetType = TargetType.USER)
    public ReportResponse resolveReport(Long reportId, ResolveReportRequest request, Long adminId) {
        Report report = findReportOrThrow(reportId);

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new InvalidReportStatusException("Report is already resolved.");
        }

        report.setStatus(ReportStatus.RESOLVED);
        report.setReviewedByAdminId(adminId);
        report.setReviewedAt(LocalDateTime.now());
        report.setResolutionNote(request.getResolutionNote());

        Report updated = reportRepository.save(report);
        log.info("Report {} resolved by admin {} with action: {}", reportId, adminId, request.getActionToTake());
        return reportMapper.toResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.REPORT_REJECTED, targetType = TargetType.USER)
    public ReportResponse rejectReport(Long reportId, String resolutionNote, Long adminId) {
        Report report = findReportOrThrow(reportId);

        if (report.getStatus() == ReportStatus.RESOLVED || report.getStatus() == ReportStatus.REJECTED) {
            throw new InvalidReportStatusException("Report is already " + report.getStatus());
        }

        report.setStatus(ReportStatus.REJECTED);
        report.setReviewedByAdminId(adminId);
        report.setReviewedAt(LocalDateTime.now());
        report.setResolutionNote(resolutionNote);

        Report updated = reportRepository.save(report);
        log.info("Report {} rejected by admin {}", reportId, adminId);
        return reportMapper.toResponse(updated);
    }

    private Report findReportOrThrow(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
    }
}

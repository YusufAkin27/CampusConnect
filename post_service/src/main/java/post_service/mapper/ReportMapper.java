package post_service.mapper;

import org.springframework.stereotype.Component;
import post_service.dto.response.ReportResponse;
import post_service.entity.PostReport;

/**
 * Manual mapper for Report entities to ReportResponse DTO.
 */
@Component
public class ReportMapper {

    public ReportResponse toPostReportResponse(PostReport report) {
        if (report == null) return null;
        return ReportResponse.builder()
                .id(report.getId())
                .targetId(report.getPost() != null ? report.getPost().getId() : null)
                .targetType("POST")
                .reporterAuthUserId(report.getReporterAuthUserId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .reviewedAt(report.getReviewedAt())
                .build();
    }
}

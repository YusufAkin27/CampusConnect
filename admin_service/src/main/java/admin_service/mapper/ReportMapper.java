package admin_service.mapper;

import admin_service.dto.response.ReportResponse;
import admin_service.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report entity) {
        if (entity == null) return null;
        return ReportResponse.builder()
                .id(entity.getId())
                .reporterUserId(entity.getReporterUserId())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .reason(entity.getReason())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .reviewedByAdminId(entity.getReviewedByAdminId())
                .reviewedAt(entity.getReviewedAt())
                .resolutionNote(entity.getResolutionNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

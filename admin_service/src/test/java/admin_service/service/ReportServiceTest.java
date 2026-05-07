package admin_service.service;

import admin_service.dto.request.CreateReportRequest;
import admin_service.dto.request.ResolveReportRequest;
import admin_service.dto.response.ReportResponse;
import admin_service.entity.Report;
import admin_service.enums.*;
import admin_service.exception.InvalidReportStatusException;
import admin_service.mapper.ReportMapper;
import admin_service.repository.ReportRepository;
import admin_service.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @Test
    void resolveReport_Success() {
        Report report = Report.builder()
                .id(1L).reporterUserId(100L).targetType(TargetType.POST)
                .targetId(200L).reason(ReportReason.SPAM).status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now()).build();

        ResolveReportRequest request = ResolveReportRequest.builder()
                .resolutionNote("Spam confirmed. Post removed.").actionToTake("DELETE_POST").build();

        ReportResponse expected = ReportResponse.builder()
                .id(1L).status(ReportStatus.RESOLVED).build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportMapper.toResponse(any())).thenReturn(expected);

        ReportResponse result = reportService.resolveReport(1L, request, 10L);

        assertNotNull(result);
        assertEquals(ReportStatus.RESOLVED, result.getStatus());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void resolveReport_AlreadyResolved_ThrowsException() {
        Report report = Report.builder()
                .id(1L).status(ReportStatus.RESOLVED).createdAt(LocalDateTime.now()).build();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        ResolveReportRequest request = ResolveReportRequest.builder().resolutionNote("note").build();

        assertThrows(InvalidReportStatusException.class, () -> reportService.resolveReport(1L, request, 10L));
    }
}

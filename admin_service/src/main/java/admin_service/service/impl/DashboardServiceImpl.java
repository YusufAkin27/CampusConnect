package admin_service.service.impl;

import admin_service.client.MediaServiceClient;
import admin_service.client.UserServiceClient;
import admin_service.client.PostServiceClient;
import admin_service.dto.response.DashboardSummaryResponse;
import admin_service.enums.ReportStatus;
import admin_service.repository.ReportRepository;
import admin_service.repository.UserBanRecordRepository;
import admin_service.service.DashboardService;
import admin_service.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserServiceClient userServiceClient;
    private final PostServiceClient postServiceClient;
    private final MediaServiceClient mediaServiceClient;
    private final ReportRepository reportRepository;
    private final UserBanRecordRepository banRecordRepository;
    private final SystemHealthService systemHealthService;

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        DashboardSummaryResponse.DashboardSummaryResponseBuilder builder = DashboardSummaryResponse.builder();

        // Report stats (local DB)
        builder.totalReports(reportRepository.count());
        builder.pendingReports(reportRepository.countByStatus(ReportStatus.PENDING));
        builder.resolvedReports(reportRepository.countByStatus(ReportStatus.RESOLVED));
        builder.bannedUsers(banRecordRepository.findByActiveTrue(org.springframework.data.domain.Pageable.unpaged()).getTotalElements());

        // User stats from user-service
        try {
            Map<String, Object> userStats = userServiceClient.getUserStats();
            builder.totalUsers(toLong(userStats.get("totalUsers")));
            builder.activeUsers(toLong(userStats.get("activeUsers")));
            builder.newUsersToday(toLong(userStats.get("newUsersToday")));
        } catch (Exception e) {
            log.warn("Failed to fetch user stats: {}", e.getMessage());
        }

        // Post stats from post-service
        try {
            Map<String, Object> postStats = postServiceClient.getPostStats();
            builder.totalPosts(toLong(postStats.get("totalPosts")));
            builder.postsToday(toLong(postStats.get("postsToday")));
            builder.hiddenPosts(toLong(postStats.get("hiddenPosts")));
            builder.deletedPosts(toLong(postStats.get("deletedPosts")));
        } catch (Exception e) {
            log.warn("Failed to fetch post stats: {}", e.getMessage());
        }

        // Media stats from media-service
        try {
            Map<String, Object> mediaStats = mediaServiceClient.getMediaStats();
            builder.totalMediaFiles(toLong(mediaStats.get("totalMediaFiles")));
            builder.totalMediaStorageSize(String.valueOf(mediaStats.getOrDefault("totalStorageSize", "0 MB")));
        } catch (Exception e) {
            log.warn("Failed to fetch media stats: {}", e.getMessage());
        }

        // System health
        try {
            var services = systemHealthService.getAllServicesHealth();
            int active = (int) services.stream().filter(s -> "UP".equals(s.getStatus())).count();
            builder.activeServices(active);
            builder.unhealthyServices(services.size() - active);
        } catch (Exception e) {
            log.warn("Failed to fetch system health: {}", e.getMessage());
        }

        return builder.build();
    }

    @Override
    public Map<String, Object> getUserStats() {
        try { return userServiceClient.getUserStats(); }
        catch (Exception e) { log.warn("User stats unavailable: {}", e.getMessage()); return Map.of(); }
    }

    @Override
    public Map<String, Object> getPostStats() {
        try { return postServiceClient.getPostStats(); }
        catch (Exception e) { log.warn("Post stats unavailable: {}", e.getMessage()); return Map.of(); }
    }

    @Override
    public Map<String, Object> getReportStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", reportRepository.count());
        stats.put("pendingReports", reportRepository.countByStatus(ReportStatus.PENDING));
        stats.put("reviewingReports", reportRepository.countByStatus(ReportStatus.REVIEWING));
        stats.put("resolvedReports", reportRepository.countByStatus(ReportStatus.RESOLVED));
        stats.put("rejectedReports", reportRepository.countByStatus(ReportStatus.REJECTED));
        return stats;
    }

    @Override
    public Map<String, Object> getMediaStats() {
        try { return mediaServiceClient.getMediaStats(); }
        catch (Exception e) { log.warn("Media stats unavailable: {}", e.getMessage()); return Map.of(); }
    }

    private long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        try { return Long.parseLong(value.toString()); } catch (Exception e) { return 0L; }
    }
}

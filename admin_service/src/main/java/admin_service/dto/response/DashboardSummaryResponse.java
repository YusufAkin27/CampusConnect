package admin_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard summary response aggregating statistics from all services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    // User statistics
    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long newUsersToday;

    // Post statistics
    private long totalPosts;
    private long postsToday;
    private long hiddenPosts;
    private long deletedPosts;

    // Report statistics
    private long totalReports;
    private long pendingReports;
    private long resolvedReports;

    // Media statistics
    private long totalMediaFiles;
    private String totalMediaStorageSize;

    // System statistics
    private int activeServices;
    private int unhealthyServices;
}

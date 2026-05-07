package admin_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Health status of a single microservice.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealthResponse {
    private String serviceName;
    private String status;
    private String host;
    private int port;
    private String healthCheckUrl;
    private LocalDateTime lastCheckedAt;
    private Long responseTimeMs;
}

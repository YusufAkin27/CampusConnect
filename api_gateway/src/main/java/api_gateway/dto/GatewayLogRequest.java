package api_gateway.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GatewayLogRequest {
    private String requestId;
    private String correlationId;
    private String method;
    private String path;
    private String query;
    private String routeId;
    private String serviceName;
    private String clientIp;
    private String userAgent;
    private String userId;
    private int statusCode;
    private long durationMs;
    private Instant timestamp;
}

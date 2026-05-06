package logging_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestLogResponse {

    private Long id;
    private String traceId;
    private String correlationId;
    private String requestId;
    private String serviceName;
    private Long authUserId;
    private String httpMethod;
    private String endpoint;
    private String queryString;
    private Integer httpStatus;
    private Long durationMs;
    private String clientIp;
    private String userAgent;
    private String requestBodyPreview;
    private String responseBodyPreview;
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private Boolean success;
    private LocalDateTime createdAt;
}

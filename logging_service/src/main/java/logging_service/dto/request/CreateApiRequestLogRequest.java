package logging_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateApiRequestLogRequest {

    private String traceId;
    private String correlationId;
    private String requestId;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    private Long authUserId;

    @NotBlank(message = "HTTP method cannot be blank")
    private String httpMethod;

    @NotBlank(message = "Endpoint cannot be blank")
    private String endpoint;

    private String queryString;

    @NotNull(message = "HTTP status cannot be null")
    private Integer httpStatus;

    private Long durationMs;
    private String clientIp;
    private String userAgent;
    private String requestBodyPreview;
    private String responseBodyPreview;
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private Boolean success;
}

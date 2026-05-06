package media_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMediaUsageRequest {

    @NotNull(message = "Media ID must not be null")
    private Long mediaId;

    @NotBlank(message = "Service name must not be blank")
    private String serviceName;

    @NotBlank(message = "Target type must not be blank")
    private String targetType;

    @NotNull(message = "Target ID must not be null")
    private Long targetId;
}

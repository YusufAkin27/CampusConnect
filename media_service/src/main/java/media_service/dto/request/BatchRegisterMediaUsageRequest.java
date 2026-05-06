package media_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRegisterMediaUsageRequest {

    @NotEmpty(message = "Usages list must not be empty")
    @Size(max = 50, message = "Maximum 50 usages can be registered in a single batch")
    @Valid
    private List<RegisterMediaUsageRequest> usages;
}

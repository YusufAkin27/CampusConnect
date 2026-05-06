package logging_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchLogEntryRequest {

    @NotEmpty(message = "Log list cannot be empty")
    @Size(max = 500, message = "Batch size cannot exceed 500 logs")
    @Valid
    private List<CreateLogEntryRequest> logs;
}

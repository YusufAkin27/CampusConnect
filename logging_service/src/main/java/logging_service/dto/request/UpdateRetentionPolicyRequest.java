package logging_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import logging_service.enums.LogCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRetentionPolicyRequest {

    @NotNull(message = "Category cannot be null")
    private LogCategory category;

    @NotNull(message = "Retention days cannot be null")
    @Min(value = 1, message = "Retention days must be at least 1")
    private Integer retentionDays;

    @Builder.Default
    private Boolean enabled = true;
}

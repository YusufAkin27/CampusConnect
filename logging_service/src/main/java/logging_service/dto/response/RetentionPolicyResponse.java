package logging_service.dto.response;

import logging_service.enums.LogCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetentionPolicyResponse {

    private Long id;
    private LogCategory category;
    private Integer retentionDays;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

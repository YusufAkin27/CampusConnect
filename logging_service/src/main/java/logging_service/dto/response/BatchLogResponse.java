package logging_service.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchLogResponse {

    private Integer receivedCount;
    private Integer savedCount;
    private Integer failedCount;
    private List<String> errors;
}

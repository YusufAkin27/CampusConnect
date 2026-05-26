package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCompletionResponse {

    private Long userId;

    private Boolean completed;


    private Integer completionRate;


    private List<String> missingFields;
}

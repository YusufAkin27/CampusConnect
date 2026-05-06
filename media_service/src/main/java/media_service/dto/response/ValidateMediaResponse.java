package media_service.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateMediaResponse {

    private Boolean valid;
    private List<MediaSummaryResponse> mediaList;
    private List<Long> invalidMediaIds;
    private String message;
}

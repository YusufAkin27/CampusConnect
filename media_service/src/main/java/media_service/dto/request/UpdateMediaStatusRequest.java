package media_service.dto.request;

import lombok.*;
import media_service.enums.MediaStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMediaStatusRequest {

    private MediaStatus status;
}

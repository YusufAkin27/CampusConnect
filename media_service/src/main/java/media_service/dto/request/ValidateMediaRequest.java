package media_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import media_service.enums.MediaContext;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateMediaRequest {

    @NotEmpty(message = "Media IDs list must not be empty")
    @Size(max = 20, message = "Maximum 20 media IDs can be validated at once")
    private List<Long> mediaIds;

    @NotNull(message = "Expected media context must not be null")
    private MediaContext expectedContext;

    /**
     * Optional: if provided, validates that the media belongs to this user.
     */
    private Long ownerAuthUserId;
}

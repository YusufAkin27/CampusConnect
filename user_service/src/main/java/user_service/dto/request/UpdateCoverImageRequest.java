package user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating cover image URL.
 * Image upload is handled by file-service; this only stores the resulting URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCoverImageRequest {

    @NotBlank(message = "Cover image URL cannot be blank")
    private String coverImageUrl;
}

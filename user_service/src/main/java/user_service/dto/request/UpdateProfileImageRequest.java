package user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating profile image URL.
 * Image upload is handled by file-service; this only stores the resulting URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileImageRequest {

    @NotBlank(message = "Profile image URL cannot be blank")
    private String profileImageUrl;
}

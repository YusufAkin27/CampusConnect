package media_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import media_service.enums.MediaContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for uploading a media file.
 * The file is received via multipart/form-data.
 *
 * Controller uses @RequestPart or @RequestParam for 'file' and @RequestParam for other fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadMediaRequest {

    /**
     * The file to upload. Cannot be null or empty.
     */
    private MultipartFile file;

    /**
     * The context of the media upload (e.g. POST_MEDIA, PROFILE_IMAGE).
     */
    @NotNull(message = "Media context must not be null")
    private MediaContext mediaContext;

    /**
     * Whether the file should be publicly accessible. Defaults to true.
     */
    private Boolean publicAccessible;
}

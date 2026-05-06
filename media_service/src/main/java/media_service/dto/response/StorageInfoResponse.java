package media_service.dto.response;

import lombok.*;
import media_service.enums.StorageProvider;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageInfoResponse {

    private StorageProvider provider;
    private String basePath;
    private Long maxImageSize;
    private Long maxVideoSize;
    private Long maxFileSize;
    private List<String> allowedImageTypes;
    private List<String> allowedVideoTypes;
    private List<String> allowedFileTypes;
}

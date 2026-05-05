package post_service.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagResponse {

    private Long id;
    private String name;
    private Long usageCount;
}

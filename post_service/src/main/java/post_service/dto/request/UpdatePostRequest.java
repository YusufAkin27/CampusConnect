package post_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import post_service.enums.PostVisibility;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

    @Size(max = 3000, message = "Content must not exceed 3000 characters")
    private String content;

    private PostVisibility visibility;

    private Boolean commentsEnabled;

    private Boolean likesEnabled;

    @Valid
    private List<PostMediaRequest> mediaList;
}

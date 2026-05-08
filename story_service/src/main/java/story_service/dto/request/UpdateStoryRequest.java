package story_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import story_service.enums.StoryVisibility;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoryRequest {

    @Size(max = 300, message = "Caption must be at most 300 characters")
    private String caption;

    private StoryVisibility visibility;
}

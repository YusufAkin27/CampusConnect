package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserProfileResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String displayName;
    private String bio;
    private String profileImageUrl;

}

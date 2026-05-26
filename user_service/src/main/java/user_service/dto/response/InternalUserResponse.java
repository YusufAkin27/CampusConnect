package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.AccountStatus;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String email;
    private String displayName;
    private String profileImageUrl;
    private AccountStatus accountStatus;
}

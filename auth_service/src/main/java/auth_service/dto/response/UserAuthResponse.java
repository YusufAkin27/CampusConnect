package auth_service.dto.response;

import auth_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /me ve validate-token endpointleri için kullanıcı kimlik bilgisi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;
}

package auth_service.mapper;

import auth_service.dto.response.UserAuthResponse;
import auth_service.entity.User;
import org.springframework.stereotype.Component;

/**
 * User entity ile DTO'lar arasında dönüşümü sağlar.
 */
@Component
public class UserMapper {

    public UserAuthResponse toUserAuthResponse(User user) {
        if (user == null) return null;

        return UserAuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

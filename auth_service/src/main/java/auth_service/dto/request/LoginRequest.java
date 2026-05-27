package auth_service.dto.request;

import auth_service.validations.ValidLoginField;
import auth_service.validations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı veya email boş olamaz")
    @ValidLoginField
    private String usernameOrEmail;

    @NotBlank(message = "Şifre boş olamaz")
    @ValidPassword
    private String password;
}

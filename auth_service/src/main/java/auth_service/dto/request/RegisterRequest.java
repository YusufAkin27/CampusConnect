package auth_service.dto.request;

import auth_service.validations.ValidEmail;
import auth_service.validations.ValidPassword;
import auth_service.validations.ValidUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @ValidUsername
    private String username;

    @NotBlank(message = "Email boş olamaz")
    @ValidEmail
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @ValidPassword
    private String password;
}

package auth_service.dto.request;

import auth_service.validations.ValidPassword;
import jakarta.validation.Valid;
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
public class ChangePasswordRequest {

    @NotBlank(message = "Mevcut şifre boş olamaz")
    private String currentPassword;

    @NotBlank(message = "Yeni şifre boş olamaz")
    @ValidPassword
    private String newPassword;

    @NotBlank(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;
}

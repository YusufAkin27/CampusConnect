package auth_service.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator
        implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d).{8,64}$";

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext context) {

        if (password == null || password.isBlank()) {
            return false;
        }

        return password.matches(PASSWORD_REGEX);
    }
}
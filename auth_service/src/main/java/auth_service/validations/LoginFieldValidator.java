package auth_service.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginFieldValidator
        implements ConstraintValidator<ValidLoginField, String> {

    private static final String USERNAME_REGEX =
            "^[a-zA-Z0-9_]{3,50}$";

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return false;
        }

        return value.matches(USERNAME_REGEX)
                || value.matches(EMAIL_REGEX);
    }
}
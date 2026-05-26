package user_service.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator
        implements ConstraintValidator<ValidUsername, String> {

    private static final String USERNAME_REGEX =
            "^[a-zA-Z0-9_]+$";

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext context) {

        if (username == null || username.isBlank()) {
            return false;
        }

        if (username.length() < 3 || username.length() > 50) {
            return false;
        }

        return username.matches(USERNAME_REGEX);
    }
}
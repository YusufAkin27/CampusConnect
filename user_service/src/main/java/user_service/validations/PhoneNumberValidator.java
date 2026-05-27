package user_service.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator
        implements ConstraintValidator<ValidPhoneNumber, String> {

    // Türkiye telefon regex
    private static final String PHONE_REGEX =
            "^(\\+90|0)?5\\d{9}$";

    @Override
    public boolean isValid(String phoneNumber,
                           ConstraintValidatorContext context) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }

        return phoneNumber.matches(PHONE_REGEX);
    }
}
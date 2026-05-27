package user_service.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface ValidPhoneNumber {

    String message() default
            "Geçerli bir Türkiye telefon numarası giriniz";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package auth_service.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {

    String message() default
            "Geçerli bir email adresi giriniz";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
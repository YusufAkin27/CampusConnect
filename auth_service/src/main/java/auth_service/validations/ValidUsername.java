package auth_service.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameValidator.class)
public @interface ValidUsername {

    String message() default
            "Username must be 3-50 characters and contain only letters, numbers, and underscores";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
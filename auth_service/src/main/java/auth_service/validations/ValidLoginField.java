package auth_service.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LoginFieldValidator.class)
public @interface ValidLoginField {

    String message() default
            "Geçerli bir kullanıcı adı veya email giriniz";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
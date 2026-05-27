package auth_service.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default
            "Şifre en az 8 karakter olmalı ve harf ile rakam içermelidir";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
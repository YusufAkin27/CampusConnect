package admin_service.security;

import admin_service.enums.ActionType;
import admin_service.enums.TargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for automatic audit logging via AOP.
 * Methods annotated with @AdminAction will have their execution
 * logged to the AdminActionLog table.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAction {

    ActionType actionType();

    TargetType targetType() default TargetType.USER;

    String description() default "";
}

package com.javabackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CommandValidator.class)
@Documented
public @interface ValidCommand {
    String message() default "Invalid command: contains unsafe operations";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
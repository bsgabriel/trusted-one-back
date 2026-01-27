package com.bsg.trustedone.annotation;

import com.bsg.trustedone.validator.ContactMethodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContactMethodValidator.class)
public @interface ValidContactMethod {

    String message() default "{contactMethod.validation.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
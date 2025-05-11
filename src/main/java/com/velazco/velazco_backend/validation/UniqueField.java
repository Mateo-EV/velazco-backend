package com.velazco.velazco_backend.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.velazco.velazco_backend.validation.impl.UniqueFieldImpl;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueFieldImpl.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {

  String message() default "Field must be unique";

  String fieldName();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

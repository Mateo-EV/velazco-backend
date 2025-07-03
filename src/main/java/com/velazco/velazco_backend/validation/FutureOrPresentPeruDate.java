package com.velazco.velazco_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.velazco.velazco_backend.validation.impl.FutureOrPresentPeruDateImpl;

@Documented
@Constraint(validatedBy = FutureOrPresentPeruDateImpl.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentPeruDate {
  String message() default "La fecha debe ser hoy o futura (hora Perú)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
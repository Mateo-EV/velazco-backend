package com.velazco.velazco_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.velazco.velazco_backend.validation.impl.QuantityAvailableValidator;

@Documented
@Constraint(validatedBy = QuantityAvailableValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface QuantityAvailable {
    String message() default "No hay suficiente stock disponible para uno o más productos.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

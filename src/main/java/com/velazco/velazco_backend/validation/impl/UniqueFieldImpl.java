package com.velazco.velazco_backend.validation.impl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.velazco.velazco_backend.validation.UniqueField;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueFieldImpl implements ConstraintValidator<UniqueField, List<?>> {
  private String fieldName;

  @Override
  public void initialize(UniqueField constraintAnnotation) {
    this.fieldName = constraintAnnotation.fieldName();
  }

  @Override
  public boolean isValid(List<?> value, ConstraintValidatorContext context) {
    if (value == null)
      return true;

    Set<Object> seen = new HashSet<>();
    for (Object item : value) {
      if (item == null)
        continue;

      try {
        Field field = item.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(item);

        if (fieldValue != null && !seen.add(fieldValue)) {
          return false;
        }
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException("Error al acceder al campo '" + fieldName + "' en " + item.getClass(), e);
      }
    }
    return true;
  }
}

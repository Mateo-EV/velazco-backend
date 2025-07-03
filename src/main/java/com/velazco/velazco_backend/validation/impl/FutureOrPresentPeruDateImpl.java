package com.velazco.velazco_backend.validation.impl;

import java.time.LocalDate;
import java.time.ZoneId;

import com.velazco.velazco_backend.validation.FutureOrPresentPeruDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FutureOrPresentPeruDateImpl implements ConstraintValidator<FutureOrPresentPeruDate, LocalDate> {
  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (value == null)
      return true;
    LocalDate todayInPeru = LocalDate.now(ZoneId.of("America/Lima"));
    return !value.isBefore(todayInPeru);
  }
}

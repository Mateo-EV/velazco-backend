package com.velazco.velazco_backend.exception;

public class GeneralBadRequestException extends RuntimeException {
  public GeneralBadRequestException(String message) {
    super(message);
  }
}

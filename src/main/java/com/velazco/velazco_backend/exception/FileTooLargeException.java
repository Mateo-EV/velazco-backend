package com.velazco.velazco_backend.exception;

public class FileTooLargeException extends RuntimeException {
  public FileTooLargeException(String message) {
    super(message);
  }
}
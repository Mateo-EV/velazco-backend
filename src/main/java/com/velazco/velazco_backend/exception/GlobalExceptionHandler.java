package com.velazco.velazco_backend.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
    logger.warn("Entity not found: {}", ex.getMessage());
    return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
    String errorMessages = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));
    logger.warn("Validation errors: {}", errorMessages);
    return buildErrorResponse(errorMessages, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
    logger.warn("Constraint violation: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(GeneralBadRequestException.class)
  public ResponseEntity<ErrorResponse> handleGeneralBadRequest(GeneralBadRequestException ex, WebRequest request) {
    logger.warn("General bad request: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
    logger.warn("Illegal argument: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(FileTooLargeException.class)
  public ResponseEntity<ErrorResponse> handleFileTooLargeException(FileTooLargeException ex, WebRequest request) {
    logger.warn("File too large: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
    logger.error("Unhandled exception: ", ex);
    return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
    return buildErrorResponse(ex.getMessage(), status, request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
    ErrorResponse error = new ErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(error, status);
  }
  
}

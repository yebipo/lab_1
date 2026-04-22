package com.antiprocrastinate.lab.exception;

import com.antiprocrastinate.lab.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
    List<ErrorResponseDto.FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(f -> ErrorResponseDto.FieldErrorDetail.builder()
            .field(f.getField())
            .message(f.getDefaultMessage())
            .build())
        .toList();

    return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
        "Ошибка валидации данных", errors);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNotFound(EntityNotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
    log.error("Internal Error: ", ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR", "Произошла внутренняя ошибка сервера", null);
  }

  private ResponseEntity<ErrorResponseDto> buildResponse(
      HttpStatus status, String code, String msg, List<ErrorResponseDto.FieldErrorDetail> errors) {
    return ResponseEntity.status(status).body(ErrorResponseDto.builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(code)
        .message(msg)
        .fieldErrors(errors)
        .build());
  }
}
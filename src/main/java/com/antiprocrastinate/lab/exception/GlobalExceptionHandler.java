package com.antiprocrastinate.lab.exception;

import com.antiprocrastinate.lab.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
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

    log.warn("Ошибка валидации данных: {}", errors);

    return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
        "Ошибка валидации данных", errors);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
      AuthenticationException ex) {
    log.warn("Ошибка аутентификации: {}", ex.getMessage());
    String message = ex instanceof BadCredentialsException ? "Неверный логин или пароль" :
        "Ошибка авторизации";
    return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
    log.warn("Доступ запрещен: {}", ex.getMessage());
    return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN",
        "У вас недостаточно прав для этого действия", null);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
    log.warn("Ресурс не найден: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), null);
  }

  @ExceptionHandler(BusinessOperationException.class)
  public ResponseEntity<ErrorResponseDto> handleBusinessOperationException(
      BusinessOperationException ex) {
    log.warn("Ошибка бизнес-логики: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException ex) {
    log.warn("Сущность БД не найдена: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", "Сущность не найдена", null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
    log.error("Internal Error: ", ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR", "Произошла внутренняя ошибка сервера", null);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleDataIntegrity(DataIntegrityViolationException ex) {
    log.error("Нарушение целостности данных БД: ", ex);
    return buildResponse(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION",
        "Нарушение целостности данных. Проверьте связи и уникальность значений.", null);
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
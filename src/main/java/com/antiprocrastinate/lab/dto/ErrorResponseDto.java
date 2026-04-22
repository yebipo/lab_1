package com.antiprocrastinate.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Унифицированный формат ошибки")
public class ErrorResponseDto {
  @Schema(description = "Время ошибки")
  private LocalDateTime timestamp;
  @Schema(description = "HTTP статус")
  private int status;
  @Schema(description = "Код ошибки")
  private String error;
  @Schema(description = "Сообщение")
  private String message;
  @Schema(description = "Ошибки валидации по полям")
  private List<FieldErrorDetail> fieldErrors;

  @Data
  @Builder
  public static class FieldErrorDetail {
    private String field;
    private String message;
  }
}
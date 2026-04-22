package com.antiprocrastinate.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "DTO Лога работы")
public class WorkLogDto {
  @Schema(description = "Уникальный идентификатор")
  private Long id;

  @NotNull(message = "ID задачи обязательно")
  @Schema(description = "ID связанной задачи", example = "1")
  private Long taskId;

  @NotNull(message = "Время начала обязательно")
  @Schema(description = "Время начала работы")
  private LocalDateTime startTime;

  @Schema(description = "Время окончания работы")
  private LocalDateTime endTime;

  @Min(value = 1, message = "Длительность должна быть больше 0")
  @Schema(description = "Длительность работы в минутах", example = "60")
  private Integer durationMinutes;

  @Schema(description = "Комментарий к логу работы")
  private String comment;

  @Schema(description = "Количество отвлечений/прерываний", example = "2")
  private Integer interruptionCount;

  @Schema(description = "Время создания записи")
  private LocalDateTime createdAt;
}
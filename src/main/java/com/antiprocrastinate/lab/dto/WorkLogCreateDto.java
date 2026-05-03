package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkLogCreateDto {
  @NotNull(message = "ID задачи обязателен")
  private Long taskId;
  @NotNull(message = "Время начала обязательно")
  private LocalDateTime startTime;
  @Min(value = 1, message = "Длительность > 0")
  private Integer durationMinutes;
  private String comment;
  private Integer interruptionCount;
}
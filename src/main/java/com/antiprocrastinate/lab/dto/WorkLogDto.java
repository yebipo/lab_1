package com.antiprocrastinate.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "DTO Лога работы")
public class WorkLogDto {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotNull(message = "ID задачи обязательно")
  private Long taskId;

  @NotNull(message = "Время начала обязательно")
  private LocalDateTime startTime;

  @Min(value = 1, message = "Длительность должна быть больше 0")
  private Integer durationMinutes;

  @Schema(description = "Вычисляемое время окончания", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime endTime;

  private String comment;
  private Integer interruptionCount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime createdAt;
}
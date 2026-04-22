package com.antiprocrastinate.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Data;

@Data
@Schema(description = "DTO Задачи")
public class TaskDto {
  @Schema(description = "Уникальный идентификатор")
  private Long id;

  @NotBlank(message = "Заголовок не может быть пустым")
  @Schema(description = "Заголовок задачи", example = "Написать тесты")
  private String title;

  @Schema(description = "Описание задачи")
  private String description;

  @NotBlank(message = "Статус не может быть пустым")
  @Pattern(regexp = "TODO|IN_PROGRESS|DONE",
      message = "Статус должен быть TODO, IN_PROGRESS или DONE")
  @Schema(description = "Статус задачи", example = "TODO")
  private String status;

  @Min(value = 0, message = "Очки фокуса не могут быть отрицательными")
  @Schema(description = "Очки фокуса", example = "10")
  private Integer focusScore;

  @NotNull(message = "ID пользователя обязательно")
  @Schema(description = "ID владельца задачи", example = "1")
  private Long userId;

  @Schema(description = "Список ID связанных навыков")
  private Set<Long> skillIds;
}
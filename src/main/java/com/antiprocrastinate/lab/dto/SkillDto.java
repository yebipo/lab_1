package com.antiprocrastinate.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO Навыка")
public class SkillDto {
  @Schema(description = "Уникальный идентификатор")
  private Long id;

  @NotBlank(message = "Название навыка не может быть пустым")
  @Schema(description = "Название навыка", example = "Java Programming")
  private String name;

  @Schema(description = "Описание навыка")
  private String description;

  @Schema(description = "URL иконки")
  private String iconUrl;

  @Min(value = 1, message = "Уровень должен быть не меньше 1")
  @Schema(description = "Текущий уровень навыка", example = "5")
  private Integer level;

  @Min(value = 0, message = "XP не может быть отрицательным")
  @Schema(description = "Текущий опыт", example = "150")
  private Integer currentXp;

  @Min(value = 1, message = "Требуемый XP должен быть больше 0")
  @Schema(description = "Требуемый опыт для следующего уровня", example = "500")
  private Integer requiredXp;

  @Schema(description = "ID категории, к которой относится навык", example = "1")
  private Long categoryId;
}
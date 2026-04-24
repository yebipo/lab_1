package com.antiprocrastinate.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO Категории")
public class CategoryDto {
  @Schema(description = "Уникальный идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank(message = "Название категории не может быть пустым")
  @Schema(description = "Название категории", example = "Работа")
  private String name;

  @NotBlank(message = "Цвет не может быть пустым")
  @Schema(description = "Цвет категории (hex)", example = "#FF0000")
  private String color;

  @Schema(description = "Описание категории", example = "Рабочие задачи")
  private String description;

  @Schema(description = "URL иконки")
  private String iconUrl;
}
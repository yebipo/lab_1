package com.antiprocrastinate.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO Пользователя")
public class UserDto {
  @Schema(description = "Уникальный идентификатор", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank(message = "Username не может быть пустым")
  @Schema(description = "Имя пользователя", example = "john_doe")
  private String username;

  @NotBlank(message = "Email не может быть пустым")
  @Email(message = "Некорректный формат email")
  @Schema(description = "Электронная почта", example = "john@example.com")
  private String email;

  @NotNull(message = "Уровень не может быть null")
  @Min(value = 1, message = "Уровень не может быть меньше 1")
  @Schema(description = "Уровень пользователя", example = "1")
  private Integer level;

  @Schema(description = "URL картинки уровня", accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String levelUrl;

  @NotNull(message = "Дневная цель не может быть null")
  @Min(value = 1, message = "Цель должна быть больше 0")
  @Schema(description = "Дневная цель в минутах", example = "120")
  private Integer dailyGoalMinutes;

  @Schema(description = "URL аватара")
  private String avatarUrl;
}
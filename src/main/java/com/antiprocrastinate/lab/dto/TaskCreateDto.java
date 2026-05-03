package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;

@Data
public class TaskCreateDto {
  @NotBlank(message = "Заголовок обязателен")
  private String title;
  private String description;
  @Min(0)
  private Integer focusScore;
  @NotNull(message = "ID пользователя обязателен")
  private Long userId;
  private Set<Long> skillIds;
}
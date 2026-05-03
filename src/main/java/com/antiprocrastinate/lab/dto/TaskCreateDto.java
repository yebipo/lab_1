package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Data;

@Data
public class TaskCreateDto {
  @NotBlank(message = "Заголовок обязателен")
  private String title;

  private String description;

  @Min(0)
  @Max(100)
  private Integer focusScore;

  private Set<Long> skillIds;
}
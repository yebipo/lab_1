package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.Data;

@Data
public class TaskCreateDto {
  @NotBlank(message = "Заголовок обязателен")
  private String title;

  private String description;

  private Set<Long> skillIds;
}
package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillCreateDto {
  @NotBlank(message = "Название обязательно")
  private String name;
  private String description;
  private String iconUrl;
  @Min(1)
  private Integer level;
  @Min(1)
  private Integer requiredXp;
  private Long categoryId;
}
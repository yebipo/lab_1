package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateDto {
  @NotBlank(message = "Название категории обязательно")
  private String name;
  @NotBlank(message = "Цвет обязателен")
  private String color;
  private String description;
  private String iconUrl;
}
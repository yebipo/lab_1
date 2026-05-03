package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDto {
  @NotBlank(message = "Username обязателен")
  private String username;
  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный email")
  private String email;
  @NotNull @Min(1)
  private Integer level;
  @NotNull @Min(1)
  private Integer dailyGoalMinutes;
  private String avatarUrl;
}
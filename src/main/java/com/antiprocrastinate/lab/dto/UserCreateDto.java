package com.antiprocrastinate.lab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

  @NotBlank(message = "Имя пользователя обязательно")
  private String username;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный email")
  private String email;

  @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
  private String password;

  private Integer dailyGoalMinutes;

  private String avatarUrl;
}
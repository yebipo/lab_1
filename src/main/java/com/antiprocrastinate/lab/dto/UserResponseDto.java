package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class UserResponseDto {
  private Long id;
  private String username;
  private String email;
  private Integer level;
  private String levelUrl;
  private Integer dailyGoalMinutes;
  private String avatarUrl;
}
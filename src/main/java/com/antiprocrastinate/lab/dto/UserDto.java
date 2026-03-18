package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class UserDto {
  private String username;
  private String email;
  private Integer level;
  private String levelUrl;
  private Integer dailyGoalMinutes;
  private String avatarUrl;
}
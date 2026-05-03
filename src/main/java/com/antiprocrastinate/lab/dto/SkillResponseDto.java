package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class SkillResponseDto {
  private Long id;
  private String name;
  private String description;
  private String iconUrl;
  private Integer level;
  private Integer currentXp;
  private Integer requiredXp;
  private Long categoryId;
}
package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class SkillResponseDto {
  private Long id;
  private String name;
  private String description;
  private Integer level;
  private Long categoryId;
  private String categoryName;
}
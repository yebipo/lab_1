package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class SkillDto {
  private Long id;
  private String name;
  private String iconUrl;
  private Long categoryId;
}
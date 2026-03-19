package com.antiprocrastinate.lab.dto;

import lombok.Data;

@Data
public class CategoryDto {
  private Long id;
  private String name;
  private String color;
  private String description;
  private String iconUrl;
}
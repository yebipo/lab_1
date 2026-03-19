package com.antiprocrastinate.lab.dto;

import java.util.Set;
import lombok.Data;

@Data
public class TaskDto {
  private Long id;
  private String title;
  private String description;
  private String status;
  private Integer focusScore;
  private Long userId;
  private Long categoryId;
  private Set<Long> skillIds;
}
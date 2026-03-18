package com.antiprocrastinate.lab.dto;

import java.util.Set;
import lombok.Data;

@Data
public class TaskDto {
  private String title;
  private String status;
  private Integer focusScore;
  private Long userId;
  private Set<Long> skillIds;
}
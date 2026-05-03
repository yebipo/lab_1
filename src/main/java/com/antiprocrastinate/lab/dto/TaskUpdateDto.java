package com.antiprocrastinate.lab.dto;

import java.util.Set;
import lombok.Data;

@Data
public class TaskUpdateDto {
  private String title;
  private String description;
  private String status;
  private Set<Long> skillIds;
}
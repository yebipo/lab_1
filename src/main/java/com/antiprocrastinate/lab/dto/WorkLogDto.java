package com.antiprocrastinate.lab.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkLogDto {
  private Long id;
  private Integer durationMinutes;
  private String comment;
  private Integer interruptionCount;
  private LocalDateTime createdAt;
  private Long userId;
  private Long taskId;
}
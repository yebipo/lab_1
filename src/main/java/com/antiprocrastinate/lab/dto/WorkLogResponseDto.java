package com.antiprocrastinate.lab.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WorkLogResponseDto {
  private Long id;
  private Long taskId;
  private LocalDateTime startTime;
  private Integer durationMinutes;
  private LocalDateTime endTime;
  private String comment;
  private Integer interruptionCount;
  private LocalDateTime createdAt;
}
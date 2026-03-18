package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.model.WorkLog;
import org.springframework.stereotype.Component;

@Component
public class WorkLogMapper {
  public WorkLogDto toDto(WorkLog log) {
    WorkLogDto dto = new WorkLogDto();
    dto.setDurationMinutes(log.getDurationMinutes());
    dto.setComment(log.getComment());
    dto.setInterruptionCount(log.getInterruptionCount());
    dto.setCreatedAt(log.getCreatedAt());
    if (log.getUser() != null) {
      dto.setUserId(log.getUser().getId());
    }
    if (log.getTask() != null) {
      dto.setTaskId(log.getTask().getId());
    }
    return dto;
  }
}
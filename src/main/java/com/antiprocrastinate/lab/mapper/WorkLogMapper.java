package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.WorkLog;
import org.springframework.stereotype.Component;

@Component
public class WorkLogMapper {
  public WorkLogDto toDto(WorkLog log) {
    if (log == null) {
      return null;
    }
    WorkLogDto dto = new WorkLogDto();
    dto.setId(log.getId());
    dto.setDurationMinutes(log.getDurationMinutes());
    dto.setComment(log.getComment());
    dto.setInterruptionCount(log.getInterruptionCount());
    dto.setCreatedAt(log.getCreatedAt());
    if (log.getTask() != null) {
      dto.setTaskId(log.getTask().getId());
    }
    return dto;
  }

  public WorkLog toEntity(WorkLogDto dto) {
    if (dto == null) {
      return null;
    }
    WorkLog log = new WorkLog();
    log.setId(dto.getId());
    log.setDurationMinutes(dto.getDurationMinutes());
    log.setComment(dto.getComment());
    log.setInterruptionCount(dto.getInterruptionCount());
    if (dto.getCreatedAt() != null) {
      log.setCreatedAt(dto.getCreatedAt());
    }
    if (dto.getTaskId() != null) {
      Task task = new Task();
      task.setId(dto.getTaskId());
      log.setTask(task);
    }
    return log;
  }
}
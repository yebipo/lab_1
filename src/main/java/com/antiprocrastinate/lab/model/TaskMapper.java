package com.antiprocrastinate.lab.model;

import com.antiprocrastinate.lab.dto.TaskDto;

public class TaskMapper {
  private TaskMapper() {
  }

  public static TaskDto toDto(Task task) {
    if (task == null) {
      return null;
    }
    TaskDto dto = new TaskDto();
    dto.setTitle(task.getTitle());
    dto.setStatus(task.getStatus());
    return dto;
  }
}
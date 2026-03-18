package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
  public TaskDto toDto(Task task) {
    TaskDto dto = new TaskDto();
    dto.setId(task.getId());
    dto.setTitle(task.getTitle());
    dto.setStatus(task.getStatus().name());
    dto.setFocusScore(task.getFocusScore());
    if (task.getUser() != null) {
      dto.setUserId(task.getUser().getId());
    }
    if (task.getSkills() != null) {
      dto.setSkillIds(task.getSkills().stream()
          .map(Skill::getId).collect(Collectors.toSet()));
    }
    return dto;
  }
}
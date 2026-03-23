package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.model.User;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

  public TaskDto toDto(Task task) {
    if (task == null) {
      return null;
    }

    TaskDto dto = new TaskDto();
    dto.setId(task.getId());
    dto.setTitle(task.getTitle());
    dto.setDescription(task.getDescription());
    dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
    dto.setFocusScore(task.getFocusScore());

    if (task.getUser() != null) {
      dto.setUserId(task.getUser().getId());
    }

    if (task.getSkills() != null) {
      dto.setSkillIds(task.getSkills().stream()
          .map(Skill::getId)
          .collect(Collectors.toSet()));
    }

    return dto;
  }

  public Task toEntity(TaskDto dto) {
    if (dto == null) {
      return null;
    }

    Task task = new Task();
    task.setId(dto.getId());
    task.setTitle(dto.getTitle());
    task.setDescription(dto.getDescription());

    if (dto.getStatus() != null) {
      task.setStatus(TaskStatus.valueOf(dto.getStatus()));
    }

    task.setFocusScore(dto.getFocusScore());

    if (dto.getUserId() != null) {
      User user = new User();
      user.setId(dto.getUserId());
      task.setUser(user);
    }

    if (dto.getSkillIds() != null) {
      task.setSkills(dto.getSkillIds().stream()
          .map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
          })
          .collect(Collectors.toSet()));
    }

    return task;
  }
}
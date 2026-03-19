package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
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

    // Собираем ID категорий через метод getCategories(), который лезет в скиллы
    // Теперь метод в Task.java используется, и IntelliJ не будет ругаться
    dto.setCategoryIds(task.getCategories().stream()
        .map(Category::getId)
        .collect(Collectors.toSet()));

    // Собираем ID скиллов
    if (task.getSkills() != null) {
      dto.setSkillIds(task.getSkills().stream()
          .map(Skill::getId)
          .collect(Collectors.toSet()));
    }

    return dto;
  }
}
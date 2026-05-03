package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;

  @Cacheable(value = "tasks", key = "{#pageable.pageNumber, #pageable.pageSize}")
  @Transactional(readOnly = true)
  public Page<Task> findAll(Pageable pageable) {
    return taskRepo.findAll(pageable);
  }

  @Cacheable(value = "tasks", key = "#id")
  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
  }

  @CacheEvict(value = "tasks", allEntries = true)
  @Transactional
  public Task save(Task task) {
    return taskRepo.save(task);
  }

  @CacheEvict(value = "tasks", allEntries = true)
  @Transactional
  public List<Task> saveAll(List<Task> tasks) {
    return taskRepo.saveAll(tasks);
  }

  @CacheEvict(value = "tasks", allEntries = true)
  @Transactional
  public List<Task> patchBulk(List<TaskDto> dtos) {
    List<Long> ids = dtos.stream().map(TaskDto::getId).toList();
    List<Task> existingTasks = taskRepo.findAllById(ids);

    if (existingTasks.size() != ids.size()) {
      throw new ResourceNotFoundException("Одна или несколько задач не найдены");
    }

    Map<Long, TaskDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(
            TaskDto::getId, dto -> dto, (existing, replacement) -> replacement));

    existingTasks.forEach(existing -> {
      TaskDto dto = dtoMap.get(existing.getId());
      if (dto.getTitle() != null) {
        existing.setTitle(dto.getTitle());
      }
      if (dto.getDescription() != null) {
        existing.setDescription(dto.getDescription());
      }
      if (dto.getStatus() != null) {
        existing.setStatus(TaskStatus.valueOf(dto.getStatus()));
      }
      if (dto.getFocusScore() != null) {
        existing.setFocusScore(dto.getFocusScore());
      }
    });

    return taskRepo.saveAll(existingTasks);
  }

  @CacheEvict(value = "tasks", allEntries = true)
  @Transactional
  public void deleteById(Long id) {
    Task task = taskRepo.findById(id)
        .orElseThrow(() ->
            new ResourceNotFoundException("Cannot delete: Task not found with id: " + id));
    taskRepo.delete(task);
  }

  @CacheEvict(value = "tasks", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteById);
  }

  @Cacheable(value = "tasks",
      key = "{#userId, #skillId, #pageable.pageNumber, #pageable.pageSize, #useNative}")
  @Transactional(readOnly = true)
  public Page<Task> getTasksFiltered(
      Long userId, Long skillId, Pageable pageable, boolean useNative) {
    return useNative
        ? taskRepo.findTasksByUserAndSkillNative(userId, skillId, pageable)
        : taskRepo.findTasksByUserAndSkillJpql(userId, skillId, pageable);
  }
}
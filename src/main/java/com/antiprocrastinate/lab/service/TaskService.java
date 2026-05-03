package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.repository.TaskSpecifications;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;
  private final TaskMapper taskMapper;

  @Transactional(readOnly = true)
  public Page<Task> findAll(Pageable pageable) {
    return taskRepo.findAll(pageable);
  }

  @Cacheable(value = "task_item", key = "#id")
  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
  }

  @CachePut(value = "task_item", key = "#result.id")
  @Transactional
  public Task create(TaskDto dto) {
    return taskRepo.save(taskMapper.toEntity(dto));
  }

  @CachePut(value = "task_item", key = "#id")
  @Transactional
  public Task update(Long id, TaskDto dto) {
    Task existing = taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    taskMapper.updateEntityFromDto(dto, existing);
    return taskRepo.save(existing);
  }

  @CacheEvict(value = "task_item", allEntries = true)
  @Transactional
  public List<Task> patchBulk(List<TaskDto> dtos) {
    List<Long> ids = dtos.stream().map(TaskDto::getId).toList();
    List<Task> existingTasks = taskRepo.findAllById(ids);
    if (existingTasks.size() != ids.size()) {
      throw new ResourceNotFoundException("One or more tasks not found");
    }
    Map<Long, TaskDto> dtoMap = dtos.stream().collect(Collectors.toMap(TaskDto::getId, d -> d));
    existingTasks.forEach(e -> taskMapper.updateEntityFromDto(dtoMap.get(e.getId()), e));
    return taskRepo.saveAll(existingTasks);
  }

  @CacheEvict(value = "task_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    deleteInternal(id);
  }

  @CacheEvict(value = "task_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteInternal);
  }

  private void deleteInternal(Long id) {
    Task task = taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    taskRepo.delete(task);
  }

  @Transactional(readOnly = true)
  public Page<Task> getTasksFiltered(Long userId, Long skillId, String status, String title, Pageable pageable) {
    Specification<Task> spec = Specification.where(TaskSpecifications.hasUserId(userId))
        .and(TaskSpecifications.hasSkillId(skillId))
        .and(TaskSpecifications.hasStatus(status))
        .and(TaskSpecifications.titleLike(title));
    return taskRepo.findAll(spec, pageable);
  }
}
package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.TaskCreateDto;
import com.antiprocrastinate.lab.dto.TaskResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.repository.TaskSpecifications;
import lombok.RequiredArgsConstructor;
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
  public Page<TaskResponseDto> findAll(Pageable pageable) {
    return taskRepo.findAll(pageable).map(taskMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public TaskResponseDto findById(Long id) {
    return taskRepo.findById(id)
        .map(taskMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
  }

  @Transactional
  public TaskResponseDto create(TaskCreateDto dto) {
    return taskMapper.toResponseDto(taskRepo.save(taskMapper.toEntity(dto)));
  }

  @Transactional
  public TaskResponseDto update(Long id, TaskCreateDto dto) {
    Task existing = taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    taskMapper.updateEntity(dto, existing);
    return taskMapper.toResponseDto(taskRepo.save(existing));
  }

  @Transactional
  public void deleteById(Long id) {
    taskRepo.deleteById(id);
  }

  @Transactional(readOnly = true)
  public Page<TaskResponseDto> getTasksFiltered(
      Long userId, Long skillId, String status, String title, Pageable pageable) {
    Specification<Task> spec = Specification.where(TaskSpecifications.hasUserId(userId))
        .and(TaskSpecifications.hasSkillId(skillId))
        .and(TaskSpecifications.hasStatus(status))
        .and(TaskSpecifications.titleLike(title));
    return taskRepo.findAll(spec, pageable).map(taskMapper::toResponseDto);
  }
}
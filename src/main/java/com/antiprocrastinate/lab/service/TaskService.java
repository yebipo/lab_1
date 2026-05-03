package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.TaskCreateDto;
import com.antiprocrastinate.lab.dto.TaskResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.repository.TaskSpecifications;
import com.antiprocrastinate.lab.repository.UserRepository;
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
  private final UserRepository userRepository;

  private static final String TASK_NOT_FOUND_MSG = "Task not found or access denied: ";

  private User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
  }

  @Transactional(readOnly = true)
  public Page<TaskResponseDto> findAll(String username, Pageable pageable) {
    Long userId = getUserByUsername(username).getId();
    return taskRepo.findAllByUserId(userId, pageable).map(taskMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public TaskResponseDto findById(Long id, String username) {
    Long userId = getUserByUsername(username).getId();
    return taskRepo.findByIdAndUserId(id, userId)
        .map(taskMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MSG + id));
  }

  @Transactional
  public TaskResponseDto create(TaskCreateDto dto, String username) {
    User user = getUserByUsername(username);
    Task task = taskMapper.toEntity(dto);
    task.setUser(user);
    task.setFocusScore(100); // Базовый фокус при создании
    return taskMapper.toResponseDto(taskRepo.save(task));
  }

  @Transactional
  public TaskResponseDto update(Long id, TaskCreateDto dto, String username) {
    Long userId = getUserByUsername(username).getId();
    Task existing = taskRepo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MSG + id));
    taskMapper.updateEntity(dto, existing);
    return taskMapper.toResponseDto(taskRepo.save(existing));
  }

  @Transactional
  public void deleteById(Long id, String username) {
    Long userId = getUserByUsername(username).getId();
    Task existing = taskRepo.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MSG + id));
    taskRepo.delete(existing);
  }

  @Transactional(readOnly = true)
  public Page<TaskResponseDto> getTasksFiltered(
      Long skillId, String status, String title, Pageable pageable, String username) {

    Long userId = getUserByUsername(username).getId();

    Specification<Task> spec = Specification.where(TaskSpecifications.hasUserId(userId))
        .and(TaskSpecifications.hasSkillId(skillId))
        .and(TaskSpecifications.hasStatus(status))
        .and(TaskSpecifications.titleLike(title));

    return taskRepo.findAll(spec, pageable).map(taskMapper::toResponseDto);
  }
}
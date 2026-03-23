package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;

  public static class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
      super(message);
    }
  }

  public static class ValidationException extends RuntimeException {
    public ValidationException(String message) {
      super(message);
    }
  }

  @Transactional(readOnly = true)
  public Set<Task> findAll() {
    return new HashSet<>(taskRepo.findAll());
  }

  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepo.findById(id)
        .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
  }

  @Transactional
  public Task save(Task task) {
    validateTask(task);
    return taskRepo.save(task);
  }

  @Transactional
  public void deleteById(Long id) {
    if (!taskRepo.existsById(id)) {
      throw new NotFoundException("Cannot delete: Task not found with id: " + id);
    }
    taskRepo.deleteById(id);
  }

  @Transactional
  public void saveMultipleWithTransaction(List<Task> tasks) {
    tasks.forEach(this::save);
  }

  public void saveMultipleWithoutTransaction(List<Task> tasks) {
    tasks.forEach(this::save);
  }

  private void validateTask(Task task) {
    if (task.getTitle() == null || task.getTitle().isBlank()) {
      throw new ValidationException("Title cannot be empty");
    }
  }
}
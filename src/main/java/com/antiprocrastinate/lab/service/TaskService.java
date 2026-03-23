package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;

  private TaskService self;

  @Autowired
  public void setSelf(@Lazy TaskService self) {
    this.self = self;
  }

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
    Task task = taskRepo.findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot delete: Task not found with id: " + id));
    taskRepo.delete(task);
  }

  @Transactional
  public void saveMultipleWithTransaction(List<Task> tasks) {
    tasks.forEach(this::save);
  }

  public void saveMultipleWithoutTransaction(List<Task> tasks) {
    tasks.forEach(task -> {
      try {
        self.save(task);
      } catch (Exception _) {
        log.warn("Failed to save task with title: {}. Skipping...", task.getTitle());
      }
    });
  }

  private void validateTask(Task task) {
    if (task.getTitle() == null || task.getTitle().isBlank()) {
      throw new ValidationException("Title cannot be empty");
    }
  }
}
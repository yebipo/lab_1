package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;

  @Transactional(readOnly = true)
  public Set<Task> findAll() {
    return new HashSet<>(taskRepo.findAll());
  }

  @Transactional(readOnly = true)
  public Set<Task> searchByTitle(String title) {
    return new HashSet<>(taskRepo.findByTitleContaining(title));
  }

  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
  }

  @Transactional
  public Task save(Task task) {
    return taskRepo.save(task);
  }

  @Transactional
  public void deleteById(Long id) {
    taskRepo.deleteById(id);
  }
}
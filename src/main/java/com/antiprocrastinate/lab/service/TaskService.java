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

  public Set<Task> findAll() {
    return new HashSet<>(taskRepo.findAll());
  }

  public Set<Task> searchByTitle(String title) {
    return new HashSet<>(taskRepo.findByTitleContaining(title));
  }

  public Task save(Task task) {
    return taskRepo.save(task);
  }

  public void deleteById(Long id) {
    taskRepo.deleteById(id);
  }

  @Transactional
  public void saveMultipleTasksWithTransaction(List<Task> tasks) {
    for (Task task : tasks) {
      if (task.getTitle() == null || task.getTitle().isEmpty()) {
        throw new IllegalArgumentException("Title is empty");
      }
      taskRepo.save(task);
    }
  }

  public void saveMultipleTasksWithoutTransaction(List<Task> tasks) {
    for (Task task : tasks) {
      if (task.getTitle() == null || task.getTitle().isEmpty()) {
        throw new IllegalArgumentException("Title is empty");
      }
      taskRepo.save(task);
    }
  }
}
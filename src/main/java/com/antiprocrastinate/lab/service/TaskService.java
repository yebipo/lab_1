package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.util.TaskSearchKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;
  private TaskService self;

  private final Map<TaskSearchKey, Page<Task>> taskIndex = new HashMap<>();

  @Autowired
  public void setSelf(@Lazy TaskService self) {
    this.self = self;
  }

  @Transactional(readOnly = true)
  public Set<Task> findAll() {
    return new HashSet<>(taskRepo.findAll());
  }

  @Transactional(readOnly = true)
  public Task findById(Long id) {
    return taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
  }

  @Transactional
  public Task save(Task task) {
    Task savedTask = taskRepo.save(task);
    invalidateIndex();
    return savedTask;
  }

  @Transactional
  public void deleteById(Long id) {
    Task task = taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cannot delete: Task not found with id: " + id));
    taskRepo.delete(task);
    invalidateIndex();
  }

  @Transactional
  public void saveMultipleWithTransaction(List<Task> tasks) {
    tasks.forEach(this::save);
  }

  public void saveMultipleWithoutTransaction(List<Task> tasks) {
    tasks.forEach(task -> {
      try {
        self.save(task);
      } catch (Exception e) {
        log.warn("Error saving task", e);
      }
    });
  }

  @Transactional(readOnly = true)
  public Page<Task> getTasksFiltered(Long userId, Long skillId,
                                     Pageable pageable, boolean useNative) {
    TaskSearchKey key = new TaskSearchKey(userId, skillId,
        pageable.getPageNumber(), pageable.getPageSize(), useNative);

    if (taskIndex.containsKey(key)) {
      return taskIndex.get(key);
    }

    Page<Task> result = useNative
        ?
        taskRepo.findTasksByUserAndSkillNative(userId, skillId, pageable) :
        taskRepo.findTasksByUserAndSkillJpql(userId, skillId, pageable);

    taskIndex.put(key, result);
    return result;
  }

  private void invalidateIndex() {
    taskIndex.clear();
  }
}
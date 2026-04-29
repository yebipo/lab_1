package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.util.TaskSearchKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepo;

  private final Map<TaskSearchKey, Page<Task>> taskIndex = new HashMap<>();

  @Transactional(readOnly = true)
  public Page<Task> findAll(Pageable pageable) {
    return taskRepo.findAll(pageable);
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
  public List<Task> saveAll(List<Task> tasks) {
    List<Task> savedTasks = taskRepo.saveAll(tasks);
    invalidateIndex();
    return savedTasks;
  }

  @Transactional
  public void deleteById(Long id) {
    Task task = taskRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Cannot delete: Task not found with id: " + id));
    taskRepo.delete(task);
    invalidateIndex();
  }

  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteById);
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
        ? taskRepo.findTasksByUserAndSkillNative(userId, skillId, pageable)
        : taskRepo.findTasksByUserAndSkillJpql(userId, skillId, pageable);

    taskIndex.put(key, result);
    return result;
  }

  private void invalidateIndex() {
    taskIndex.clear();
  }
}
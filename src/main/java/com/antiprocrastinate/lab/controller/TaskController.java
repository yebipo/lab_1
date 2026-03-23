package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.service.TaskService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @GetMapping
  public Set<TaskDto> getAll() {
    return taskService.findAll().stream()
        .map(taskMapper::toDto)
        .collect(Collectors.toSet());
  }

  @PostMapping
  public TaskDto create(@RequestBody TaskDto dto) {
    Task task = taskMapper.toEntity(dto);
    return taskMapper.toDto(taskService.save(task));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    taskService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/bulk-transactional")
  public ResponseEntity<String> createBulkTransactional(@RequestBody List<TaskDto> taskDtos) {
    try {
      List<Task> tasks = taskDtos.stream()
          .map(taskMapper::toEntity)
          .toList();
      taskService.saveMultipleWithTransaction(tasks);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/bulk-non-transactional")
  public ResponseEntity<String> createBulkNonTransactional(@RequestBody List<TaskDto> taskDtos) {
    try {
      List<Task> tasks = taskDtos.stream()
          .map(taskMapper::toEntity)
          .toList();
      taskService.saveMultipleWithoutTransaction(tasks);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
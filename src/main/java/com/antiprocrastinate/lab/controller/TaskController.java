package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.service.TaskService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @GetMapping
  public Set<TaskDto> getAll(@RequestParam(required = false) String title) {
    Set<Task> tasks;
    if (title != null && !title.isEmpty()) {
      tasks = taskService.searchByTitle(title);
    } else {
      tasks = taskService.findAll();
    }
    return tasks.stream()
        .map(taskMapper::toDto)
        .collect(Collectors.toSet());
  }

  @GetMapping("/{id}")
  public TaskDto getById(@PathVariable Long id) {
    return taskMapper.toDto(taskService.findById(id));
  }

  @PostMapping
  public TaskDto create(@RequestBody Task task) {
    return taskMapper.toDto(taskService.save(task));
  }

  @PutMapping("/{id}")
  public TaskDto update(@PathVariable Long id, @RequestBody Task task) {
    task.setId(id);
    return taskMapper.toDto(taskService.save(task));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    taskService.deleteById(id);
  }
}
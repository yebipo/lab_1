package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskMapper;
import com.antiprocrastinate.lab.service.TaskService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/{id}")
  public TaskDto getTaskById(@PathVariable Long id) {
    Task task = taskService.getTaskById(id);
    return TaskMapper.toDto(task);
  }

  @GetMapping("/search")
  public List<TaskDto> searchTasks(@RequestParam String title) {
    return taskService.searchTasksByTitle(title).stream()
        .map(TaskMapper::toDto)
        .toList();
  }

}
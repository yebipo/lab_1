package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами")
public class TaskController {
  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @GetMapping
  @Operation(summary = "Получить все задачи")
  public Set<TaskDto> getAll() {
    return taskService.findAll().stream()
        .map(taskMapper::toDto)
        .collect(Collectors.toSet());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по ID")
  public TaskDto getById(@PathVariable Long id) {
    return taskMapper.toDto(taskService.findById(id));
  }

  @GetMapping("/search")
  @Operation(summary = "Поиск и фильтрация задач с пагинацией")
  public Page<TaskDto> search(
      @RequestParam Long userId,
      @RequestParam Long skillId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "false") boolean useNative
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Task> tasks = taskService.getTasksFiltered(userId, skillId, pageable, useNative);
    return tasks.map(taskMapper::toDto);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новую задачу")
  public TaskDto create(@Valid @RequestBody TaskDto dto) {
    Task task = taskMapper.toEntity(dto);
    return taskMapper.toDto(taskService.save(task));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить существующую задачу")
  public TaskDto update(@PathVariable Long id, @Valid @RequestBody TaskDto dto) {
    Task task = taskMapper.toEntity(dto);
    task.setId(id);
    return taskMapper.toDto(taskService.save(task));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить задачу")
  public void delete(@PathVariable Long id) {
    taskService.deleteById(id);
  }

  @PostMapping("/bulk-transactional")
  @Operation(summary = "Массовое создание задач (с транзакцией)")
  public ResponseEntity<String> createBulkTransactional(
      @Valid @RequestBody List<TaskDto> taskDtos) {
    try {
      List<Task> tasks = taskDtos.stream().map(taskMapper::toEntity).toList();
      taskService.saveMultipleWithTransaction(tasks);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/bulk-non-transactional")
  @Operation(summary = "Массовое создание задач (без транзакции)")
  public ResponseEntity<String> createBulkNonTransactional(
      @Valid @RequestBody List<TaskDto> taskDtos) {
    try {
      List<Task> tasks = taskDtos.stream().map(taskMapper::toEntity).toList();
      taskService.saveMultipleWithoutTransaction(tasks);
      return ResponseEntity.ok("Success");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
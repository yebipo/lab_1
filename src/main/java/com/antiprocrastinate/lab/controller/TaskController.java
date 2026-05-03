package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
  @Operation(summary = "Получить все задачи (с пагинацией)")
  public PageResponse<TaskDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
    return PageResponse.of(taskService.findAll(pageable).map(taskMapper::toDto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить задачу по ID")
  public TaskDto getById(@PathVariable Long id) {
    return taskMapper.toDto(taskService.findById(id));
  }

  @GetMapping("/search")
  @Operation(summary = "Универсальный поиск и фильтрация задач")
  public PageResponse<TaskDto> search(
      @RequestParam(required = false) Long userId,
      @RequestParam(required = false) Long skillId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String title,
      @ParameterObject @PageableDefault Pageable pageable
  ) {
    return PageResponse.of(taskService.getTasksFiltered(userId, skillId, status, title, pageable)
        .map(taskMapper::toDto));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новую задачу")
  public TaskDto create(@Valid @RequestBody TaskDto dto) {
    return taskMapper.toDto(taskService.create(dto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить существующую задачу")
  public TaskDto update(@PathVariable Long id, @Valid @RequestBody TaskDto dto) {
    return taskMapper.toDto(taskService.update(id, dto));
  }

  @PatchMapping("/bulk")
  @Operation(summary = "Массовое частичное обновление задач")
  public List<TaskDto> patchBulk(@RequestBody List<TaskDto> dtos) {
    return taskService.patchBulk(dtos).stream()
        .map(taskMapper::toDto)
        .toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить задачу")
  public void delete(@PathVariable Long id) {
    taskService.deleteById(id);
  }
}
package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.TaskCreateDto;
import com.antiprocrastinate.lab.dto.TaskResponseDto;
import com.antiprocrastinate.lab.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
public class TaskController {
  private final TaskService taskService;

  @GetMapping
  public PageResponse<TaskResponseDto> getAll(Pageable pageable) {
    return PageResponse.of(taskService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public TaskResponseDto getById(@PathVariable Long id) {
    return taskService.findById(id);
  }

  @GetMapping("/search")
  public PageResponse<TaskResponseDto> search(
      @RequestParam(required = false) Long skillId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String title,
      Pageable pageable) {
    return PageResponse.of(taskService.getTasksFiltered(skillId, status, title, pageable));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponseDto create(@Valid @RequestBody TaskCreateDto dto) {
    return taskService.create(dto);
  }

  @PutMapping("/{id}")
  public TaskResponseDto update(@PathVariable Long id, @Valid @RequestBody TaskCreateDto dto) {
    return taskService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    taskService.deleteById(id);
  }
}
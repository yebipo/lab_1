package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.config.security.CustomUserDetails;
import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.TaskCreateDto;
import com.antiprocrastinate.lab.dto.TaskResponseDto;
import com.antiprocrastinate.lab.dto.TaskUpdateDto;
import com.antiprocrastinate.lab.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  public PageResponse<TaskResponseDto> getAll(
      @AuthenticationPrincipal CustomUserDetails user, Pageable pageable) {
    return PageResponse.of(taskService.findAll(user.getId(), pageable));
  }

  @GetMapping("/{id}")
  public TaskResponseDto getById(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
    return taskService.findById(id, user.getId());
  }

  @GetMapping("/search")
  public PageResponse<TaskResponseDto> search(
      @RequestParam(required = false) Long skillId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String title,
      @AuthenticationPrincipal CustomUserDetails user,
      Pageable pageable) {
    return PageResponse.of(taskService.getTasksFiltered(
        skillId, status, title, pageable, user.getId()));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponseDto create(
      @Valid @RequestBody TaskCreateDto dto, @AuthenticationPrincipal CustomUserDetails user) {
    return taskService.create(dto, user.getId());
  }

  @PutMapping("/{id}")
  public TaskResponseDto update(
      @PathVariable Long id,
      @Valid @RequestBody TaskUpdateDto dto,
      @AuthenticationPrincipal CustomUserDetails user) {
    return taskService.update(id, dto, user.getId());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
    taskService.deleteById(id, user.getId());
  }
}
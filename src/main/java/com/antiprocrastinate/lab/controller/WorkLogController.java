package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.config.security.CustomUserDetails;
import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.WorkLogCreateDto;
import com.antiprocrastinate.lab.dto.WorkLogResponseDto;
import com.antiprocrastinate.lab.service.WorkLogService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
public class WorkLogController {
  private final WorkLogService workLogService;

  @GetMapping
  public PageResponse<WorkLogResponseDto> getAll(
      @AuthenticationPrincipal CustomUserDetails user, Pageable pageable) {
    return PageResponse.of(workLogService.findAll(user.getId(), pageable));
  }

  @GetMapping("/{id}")
  public WorkLogResponseDto getById(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
    return workLogService.findById(id, user.getId());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorkLogResponseDto create(
      @Valid @RequestBody WorkLogCreateDto dto, @AuthenticationPrincipal CustomUserDetails user) {
    return workLogService.create(dto, user.getId());
  }

  @PutMapping("/{id}")
  public WorkLogResponseDto update(
      @PathVariable Long id,
      @Valid @RequestBody WorkLogCreateDto dto,
      @AuthenticationPrincipal CustomUserDetails user) {
    return workLogService.update(id, dto, user.getId());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
    workLogService.deleteById(id, user.getId());
  }
}
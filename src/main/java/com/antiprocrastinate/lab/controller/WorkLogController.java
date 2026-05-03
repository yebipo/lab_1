package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.WorkLogCreateDto;
import com.antiprocrastinate.lab.dto.WorkLogResponseDto;
import com.antiprocrastinate.lab.service.WorkLogService;
import jakarta.validation.Valid;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
public class WorkLogController {
  private final WorkLogService workLogService;

  @GetMapping
  public PageResponse<WorkLogResponseDto> getAll(Principal principal, Pageable pageable) {
    return PageResponse.of(workLogService.findAll(principal.getName(), pageable));
  }

  @GetMapping("/{id}")
  public WorkLogResponseDto getById(@PathVariable Long id, Principal principal) {
    return workLogService.findById(id, principal.getName());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorkLogResponseDto create(
      @Valid @RequestBody WorkLogCreateDto dto, Principal principal) {
    return workLogService.create(dto, principal.getName());
  }

  @PutMapping("/{id}")
  public WorkLogResponseDto update(
      @PathVariable Long id, @Valid @RequestBody WorkLogCreateDto dto, Principal principal) {
    return workLogService.update(id, dto, principal.getName());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, Principal principal) {
    workLogService.deleteById(id, principal.getName());
  }
}
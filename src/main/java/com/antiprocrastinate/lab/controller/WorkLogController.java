package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.service.WorkLogService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
@Tag(name = "Логи работы", description = "Управление записями о проделанной работе")
public class WorkLogController {
  private final WorkLogService workLogService;
  private final WorkLogMapper workLogMapper;

  @GetMapping
  @Operation(summary = "Получить все логи работы (с пагинацией)")
  public PageResponse<WorkLogDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
    return PageResponse.of(workLogService.findAll(pageable).map(workLogMapper::toDto));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить лог работы по ID")
  public WorkLogDto getById(@PathVariable Long id) {
    return workLogMapper.toDto(workLogService.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новый лог работы")
  public WorkLogDto create(@Valid @RequestBody WorkLogDto dto) {
    return workLogMapper.toDto(workLogService.create(dto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить лог работы")
  public WorkLogDto update(@PathVariable Long id, @Valid @RequestBody WorkLogDto dto) {
    return workLogMapper.toDto(workLogService.update(id, dto));
  }

  @PatchMapping("/bulk")
  @Operation(summary = "Массовое частичное обновление логов работы")
  public List<WorkLogDto> patchBulk(@RequestBody List<WorkLogDto> dtos) {
    return workLogService.patchBulk(dtos).stream()
        .map(workLogMapper::toDto)
        .toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить лог работы")
  public void delete(@PathVariable Long id) {
    workLogService.deleteById(id);
  }
}
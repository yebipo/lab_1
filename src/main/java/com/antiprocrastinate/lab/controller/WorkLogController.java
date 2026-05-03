package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/worklogs")
@RequiredArgsConstructor
@Tag(name = "Логи работы", description = "Управление записями о проделанной работе")
public class WorkLogController {
  private final WorkLogService workLogService;
  private final WorkLogMapper workLogMapper;

  @GetMapping
  @Operation(summary = "Получить все логи работы (с пагинацией)")
  public Page<WorkLogDto> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return workLogService.findAll(PageRequest.of(page, size)).map(workLogMapper::toDto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить лог работы по ID")
  public WorkLogDto getById(@PathVariable Long id) {
    return workLogMapper.toDto(workLogService.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новый лог работы")
  public WorkLogDto create(@Valid @RequestBody WorkLogDto workLogDto) {
    WorkLog workLog = workLogMapper.toEntity(workLogDto);
    return workLogMapper.toDto(workLogService.save(workLog));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить лог работы")
  public WorkLogDto update(@PathVariable Long id, @Valid @RequestBody WorkLogDto workLogDto) {
    WorkLog workLog = workLogMapper.toEntity(workLogDto);
    workLog.setId(id);
    return workLogMapper.toDto(workLogService.save(workLog));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить лог работы")
  public void delete(@PathVariable Long id) {
    workLogService.deleteById(id);
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Массовое создание логов работы")
  public List<WorkLogDto> createBulk(@Valid @RequestBody List<WorkLogDto> dtos) {
    List<WorkLog> logs = dtos.stream().map(workLogMapper::toEntity).toList();
    return workLogService.saveAll(logs).stream().map(workLogMapper::toDto).toList();
  }

  @PutMapping("/bulk")
  @Operation(summary = "Массовое обновление логов работы (полное)")
  public List<WorkLogDto> updateBulk(@Valid @RequestBody List<WorkLogDto> dtos) {
    List<WorkLog> logs = dtos.stream().map(workLogMapper::toEntity).toList();
    return workLogService.saveAll(logs).stream().map(workLogMapper::toDto).toList();
  }

  @PatchMapping("/bulk")
  @Operation(summary = "Массовое обновление логов работы (частичное)")
  public List<WorkLogDto> patchBulk(@RequestBody List<WorkLogDto> dtos) {
    return workLogService.patchBulk(dtos).stream().map(workLogMapper::toDto).toList();
  }

  @DeleteMapping("/bulk")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Массовое удаление логов работы")
  public void deleteBulk(@RequestBody List<Long> ids) {
    workLogService.deleteAll(ids);
  }
}
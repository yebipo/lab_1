package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.service.WorkLogService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work-logs")
@RequiredArgsConstructor
public class WorkLogController {
  private final WorkLogService workLogService;
  private final WorkLogMapper workLogMapper;

  @GetMapping
  public Set<WorkLogDto> getAll() {
    return workLogService.findAll().stream()
        .map(workLogMapper::toDto)
        .collect(Collectors.toSet());
  }

  @GetMapping("/{id}")
  public WorkLogDto getById(@PathVariable Long id) {
    return workLogMapper.toDto(workLogService.findById(id));
  }

  @PostMapping
  public WorkLogDto create(@RequestBody WorkLog workLog) {
    return workLogMapper.toDto(workLogService.save(workLog));
  }

  @PutMapping("/{id}")
  public WorkLogDto update(@PathVariable Long id, @RequestBody WorkLog workLog) {
    workLog.setId(id);
    return workLogMapper.toDto(workLogService.save(workLog));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    workLogService.deleteById(id);
  }
}
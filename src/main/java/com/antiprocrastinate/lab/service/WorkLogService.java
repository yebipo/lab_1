package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.WorkLogCreateDto;
import com.antiprocrastinate.lab.dto.WorkLogResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;
  private final WorkLogMapper workLogMapper;

  @Transactional(readOnly = true)
  public Page<WorkLogResponseDto> findAll(Pageable pageable) {
    return workLogRepository.findAll(pageable).map(workLogMapper::toResponseDto);
  }

  @Cacheable(value = "worklog_item", key = "#id")
  @Transactional(readOnly = true)
  public WorkLogResponseDto findById(Long id) {
    return workLogRepository.findById(id)
        .map(workLogMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found: " + id));
  }

  @Transactional
  public WorkLogResponseDto create(WorkLogCreateDto dto) {
    return workLogMapper.toResponseDto(workLogRepository.save(workLogMapper.toEntity(dto)));
  }

  @CacheEvict(value = "worklog_item", key = "#id")
  @Transactional
  public WorkLogResponseDto update(Long id, WorkLogCreateDto dto) {
    WorkLog existing = workLogRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found: " + id));
    workLogMapper.updateEntity(dto, existing);
    return workLogMapper.toResponseDto(workLogRepository.save(existing));
  }

  @CacheEvict(value = "worklog_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    workLogRepository.deleteById(id);
  }
}
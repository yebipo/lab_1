package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;

  @Cacheable(value = "worklogs_page", key = "{#pageable.pageNumber, #pageable.pageSize}")
  @Transactional(readOnly = true)
  public Page<WorkLog> findAll(Pageable pageable) {
    return workLogRepository.findAll(pageable);
  }

  @Cacheable(value = "worklog_item", key = "#id")
  @Transactional(readOnly = true)
  public WorkLog findById(Long id) {
    return workLogRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found with id: " + id));
  }

  @Caching(
      put = { @CachePut(value = "worklog_item", key = "#result.id") },
      evict = { @CacheEvict(value = "worklogs_page", allEntries = true) }
  )
  @Transactional
  public WorkLog save(WorkLog workLog) {
    return workLogRepository.save(workLog);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "worklog_item", allEntries = true),
          @CacheEvict(value = "worklogs_page", allEntries = true)
      }
  )
  @Transactional
  public List<WorkLog> saveAll(List<WorkLog> workLogs) {
    return workLogRepository.saveAll(workLogs);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "worklog_item", allEntries = true),
          @CacheEvict(value = "worklogs_page", allEntries = true)
      }
  )
  @Transactional
  public List<WorkLog> patchBulk(List<WorkLogDto> dtos) {
    List<Long> ids = dtos.stream().map(WorkLogDto::getId).toList();
    List<WorkLog> existingLogs = workLogRepository.findAllById(ids);

    if (existingLogs.size() != ids.size()) {
      throw new ResourceNotFoundException("Один или несколько логов не найдены");
    }

    Map<Long, WorkLogDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(
            WorkLogDto::getId, dto -> dto, (existing, replacement) -> replacement));

    existingLogs.forEach(existing -> {
      WorkLogDto dto = dtoMap.get(existing.getId());
      if (dto.getStartTime() != null) {
        existing.setStartTime(dto.getStartTime());
      }
      if (dto.getEndTime() != null) {
        existing.setEndTime(dto.getEndTime());
      }
      if (dto.getDurationMinutes() != null) {
        existing.setDurationMinutes(dto.getDurationMinutes());
      }
      if (dto.getComment() != null) {
        existing.setComment(dto.getComment());
      }
      if (dto.getInterruptionCount() != null) {
        existing.setInterruptionCount(dto.getInterruptionCount());
      }
    });

    return workLogRepository.saveAll(existingLogs);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "worklog_item", key = "#id"),
          @CacheEvict(value = "worklogs_page", allEntries = true)
      }
  )
  @Transactional
  public void deleteById(Long id) {
    workLogRepository.deleteById(id);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "worklog_item", allEntries = true),
          @CacheEvict(value = "worklogs_page", allEntries = true)
      }
  )
  @Transactional
  public void deleteAll(List<Long> ids) {
    workLogRepository.deleteAllByIdInBatch(ids);
  }
}
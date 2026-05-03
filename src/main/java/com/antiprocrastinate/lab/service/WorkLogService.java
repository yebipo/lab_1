package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
  public Page<WorkLog> findAll(Pageable pageable) {
    return workLogRepository.findAll(pageable);
  }

  @Cacheable(value = "worklog_item", key = "#id")
  @Transactional(readOnly = true)
  public WorkLog findById(Long id) {
    return workLogRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found with id: " + id));
  }

  @CachePut(value = "worklog_item", key = "#result.id")
  @Transactional
  public WorkLog create(WorkLogDto dto) {
    return workLogRepository.save(workLogMapper.toEntity(dto));
  }

  @CachePut(value = "worklog_item", key = "#id")
  @Transactional
  public WorkLog update(Long id, WorkLogDto dto) {
    WorkLog existing = workLogRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found with id: " + id));
    workLogMapper.updateEntityFromDto(dto, existing);
    return workLogRepository.save(existing);
  }

  @CacheEvict(value = "worklog_item", allEntries = true)
  @Transactional
  public List<WorkLog> patchBulk(List<WorkLogDto> dtos) {
    List<Long> ids = dtos.stream().map(WorkLogDto::getId).toList();
    List<WorkLog> entities = workLogRepository.findAllById(ids);
    if (entities.size() != ids.size()) {
      throw new ResourceNotFoundException("One or more logs not found");
    }
    Map<Long, WorkLogDto> dtoMap = dtos.stream().collect(
        Collectors.toMap(WorkLogDto::getId, d -> d));
    entities.forEach(e -> workLogMapper.updateEntityFromDto(dtoMap.get(e.getId()), e));
    return workLogRepository.saveAll(entities);
  }

  @CacheEvict(value = "worklog_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    workLogRepository.deleteById(id);
  }

  @CacheEvict(value = "worklog_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    workLogRepository.deleteAllByIdInBatch(ids);
  }
}
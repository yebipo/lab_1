package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.WorkLogCreateDto;
import com.antiprocrastinate.lab.dto.WorkLogResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.TaskRepository;
import com.antiprocrastinate.lab.repository.UserRepository;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final WorkLogMapper workLogMapper;

  private static final String WORKLOG_NOT_FOUND_MSG = "WorkLog not found: ";

  private Long getCurrentUserId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Ошибка сессии пользователя"))
        .getId();
  }

  private void verifyTaskOwnership(Long taskId) {
    Long userId = getCurrentUserId();
    taskRepository.findByIdAndUserId(taskId, userId)
        .orElseThrow(() -> new AccessDeniedException(
            "Доступ к задаче запрещен или она не существует"));
  }

  @Transactional(readOnly = true)
  public Page<WorkLogResponseDto> findAll(Pageable pageable) {
    return workLogRepository.findAllByTaskUserId(getCurrentUserId(), pageable)
        .map(workLogMapper::toResponseDto);
  }

  @Cacheable(value = "worklog_item", key = "#id")
  @Transactional(readOnly = true)
  public WorkLogResponseDto findById(Long id) {
    return workLogRepository.findByIdAndTaskUserId(id, getCurrentUserId())
        .map(workLogMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));
  }

  @Transactional
  public WorkLogResponseDto create(WorkLogCreateDto dto) {
    verifyTaskOwnership(dto.getTaskId());
    return workLogMapper.toResponseDto(workLogRepository.save(workLogMapper.toEntity(dto)));
  }

  @CacheEvict(value = "worklog_item", key = "#id")
  @Transactional
  public WorkLogResponseDto update(Long id, WorkLogCreateDto dto) {
    verifyTaskOwnership(dto.getTaskId());
    WorkLog existing = workLogRepository.findByIdAndTaskUserId(id, getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));

    workLogMapper.updateEntity(dto, existing);
    return workLogMapper.toResponseDto(workLogRepository.save(existing));
  }

  @CacheEvict(value = "worklog_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    WorkLog existing = workLogRepository.findByIdAndTaskUserId(id, getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));
    workLogRepository.delete(existing);
  }
}
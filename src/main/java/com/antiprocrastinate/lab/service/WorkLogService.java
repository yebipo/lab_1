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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

  private Long getUserIdByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"))
        .getId();
  }

  private void verifyTaskOwnership(Long taskId, Long userId) {
    taskRepository.findByIdAndUserId(taskId, userId)
        .orElseThrow(() -> new AccessDeniedException(
            "Доступ к задаче запрещен или она не существует"));
  }

  @Transactional(readOnly = true)
  public Page<WorkLogResponseDto> findAll(String username, Pageable pageable) {
    Long userId = getUserIdByUsername(username);
    return workLogRepository.findAllByTaskUserId(userId, pageable)
        .map(workLogMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public WorkLogResponseDto findById(Long id, String username) {
    Long userId = getUserIdByUsername(username);
    return workLogRepository.findByIdAndTaskUserId(id, userId)
        .map(workLogMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));
  }

  @Transactional
  public WorkLogResponseDto create(WorkLogCreateDto dto, String username) {
    Long userId = getUserIdByUsername(username);
    verifyTaskOwnership(dto.getTaskId(), userId);

    WorkLog workLog = workLogMapper.toEntity(dto);
    return workLogMapper.toResponseDto(workLogRepository.save(workLog));
  }

  @Transactional
  public WorkLogResponseDto update(Long id, WorkLogCreateDto dto, String username) {
    Long userId = getUserIdByUsername(username);
    verifyTaskOwnership(dto.getTaskId(), userId);

    WorkLog existing = workLogRepository.findByIdAndTaskUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));

    workLogMapper.updateEntity(dto, existing);
    return workLogMapper.toResponseDto(workLogRepository.save(existing));
  }

  @Transactional
  public void deleteById(Long id, String username) {
    Long userId = getUserIdByUsername(username);
    WorkLog existing = workLogRepository.findByIdAndTaskUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException(WORKLOG_NOT_FOUND_MSG + id));
    workLogRepository.delete(existing);
  }
}
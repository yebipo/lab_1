package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;

  @Transactional(readOnly = true)
  public Page<WorkLog> findAll(Pageable pageable) {
    return workLogRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public WorkLog findById(Long id) {
    return workLogRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("WorkLog not found with id: " + id));
  }

  @Transactional
  public WorkLog save(WorkLog workLog) {
    return workLogRepository.save(workLog);
  }

  @Transactional
  public void deleteById(Long id) {
    workLogRepository.deleteById(id);
  }
}
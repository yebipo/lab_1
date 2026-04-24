package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;

  @Transactional(readOnly = true)
  public Set<WorkLog> findAll() {
    return new HashSet<>(workLogRepository.findAll());
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
package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkLogService {
  private final WorkLogRepository workLogRepository;

  public Set<WorkLog> findAll() {
    return new HashSet<>(workLogRepository.findAll());
  }

  public WorkLog save(WorkLog workLog) {
    return workLogRepository.save(workLog);
  }

  public void deleteById(Long id) {
    workLogRepository.deleteById(id);
  }
}
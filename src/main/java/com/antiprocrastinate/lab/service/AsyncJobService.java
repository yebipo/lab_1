package com.antiprocrastinate.lab.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncJobService {

  private final Map<String, String> jobStatuses = new ConcurrentHashMap<>();

  @Async
  public void processLongTask(String taskId, Long duration) {
    jobStatuses.put(taskId, "IN_PROGRESS");
    log.info("Task {} started with duration {} ms", taskId, duration);
    try {
      TimeUnit.MILLISECONDS.sleep(duration);
      jobStatuses.put(taskId, "COMPLETED");
      log.info("Task {} completed", taskId);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      jobStatuses.put(taskId, "FAILED");
      log.error("Task {} interrupted", taskId);
    }
  }

  public String getStatus(String taskId) {
    return jobStatuses.getOrDefault(taskId, "NOT_FOUND");
  }
}
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
  public void processLongTask(String jobId, Long durationMs) {
    jobStatuses.put(jobId, "IN_PROGRESS");
    log.info("Job {} started with duration {} ms", jobId, durationMs);
    try {
      TimeUnit.MILLISECONDS.sleep(durationMs);
      jobStatuses.put(jobId, "COMPLETED");
      log.info("Job {} completed", jobId);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      jobStatuses.put(jobId, "FAILED");
      log.error("Job {} interrupted", jobId);
    }
  }

  public String getStatus(String jobId) {
    return jobStatuses.getOrDefault(jobId, "NOT_FOUND");
  }
}
package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.service.AsyncJobService;
import com.antiprocrastinate.lab.service.ConcurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async")
@RequiredArgsConstructor
@Tag(name = "Async & Concurrency", description = "Демонстрация асинхронности и многопоточности")
public class AsyncController {
  private final AsyncJobService asyncJobService;
  private final ConcurrencyService concurrencyService;

  @PostMapping("/process/{taskId}")
  @Operation(summary = "Запустить асинхронную задачу")
  public String startProcess(@PathVariable Long taskId) {
    String jobId = UUID.randomUUID().toString();
    asyncJobService.processLongTask(jobId, taskId);
    return jobId;
  }

  @GetMapping("/status/{jobId}")
  @Operation(summary = "Проверить статус выполнения")
  public String checkStatus(@PathVariable String jobId) {
    return asyncJobService.getStatus(jobId);
  }

  @GetMapping("/demo/race-condition")
  @Operation(summary = "Демонстрация Race Condition (64 потока)")
  public String demoRace() {
    return concurrencyService.runRaceConditionDemo();
  }
}
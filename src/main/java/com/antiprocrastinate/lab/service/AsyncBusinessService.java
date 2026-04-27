package com.antiprocrastinate.lab.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncBusinessService {

  @Async
  public CompletableFuture<Long> processTaskAsync(Long taskId) {
    log.info("Начало асинхронной обработки задачи ID: {}", taskId);
    try {
      boolean isCountedDown = new CountDownLatch(1).await(2, TimeUnit.SECONDS);

      log.debug("Имитация задержки завершена. Состояние latch: {}", isCountedDown);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Асинхронная задача была прервана", e);
    }
    log.info("Асинхронная обработка задачи ID: {} завершена", taskId);

    return CompletableFuture.completedFuture(taskId);
  }
}
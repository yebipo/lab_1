package com.antiprocrastinate.lab.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConcurrencyService {

  public String runRaceConditionDemo() {
    return runRaceConditionDemo(64, 1000, 10);
  }

  public String runRaceConditionDemo(int numberOfThreads,
                                     int incrementsPerThread, int timeoutSeconds) {
    int[] unsafeCounter = {0};
    AtomicInteger safeCounter = new AtomicInteger(0);

    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    try {
      CountDownLatch startLatch = new CountDownLatch(1);
      CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

      for (int i = 0; i < numberOfThreads; i++) {
        executorService.submit(() -> {
          try {
            startLatch.await();
            for (int j = 0; j < incrementsPerThread; j++) {
              unsafeCounter[0]++;
              safeCounter.incrementAndGet();
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Рабочий поток был прерван", e);
          } finally {
            endLatch.countDown();
          }
        });
      }

      startLatch.countDown();

      boolean isCompletedInTime = endLatch.await(timeoutSeconds, TimeUnit.SECONDS);
      if (!isCompletedInTime) {
        executorService.shutdownNow();
        log.warn("Потоки не успели завершить работу за отведенное время.");
        return "Ошибка: потоки не успели завершить работу за отведенное время (таймаут).";
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Главный поток демо-сервиса был прерван", e);
      return "Ошибка: выполнение главного потока было прервано.";
    } finally {
      executorService.shutdown();
    }

    int expected = numberOfThreads * incrementsPerThread;
    return String.format(
        "Ожидалось: %d%nРезультат (Unsafe - Race Condition): %d%nРезультат (Atomic): %d",
        expected, unsafeCounter[0], safeCounter.get()
    );
  }
}
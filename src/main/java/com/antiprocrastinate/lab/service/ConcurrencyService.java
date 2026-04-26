package com.antiprocrastinate.lab.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class ConcurrencyService {
  private final AtomicInteger safeCounter = new AtomicInteger(0);

  public String runRaceConditionDemo() {
    int[] unsafeCounter = {0};
    safeCounter.set(0);

    int numberOfThreads = 64;
    int incrementsPerThread = 1000;

    try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {
      for (int i = 0; i < numberOfThreads; i++) {
        executor.submit(() -> {
          for (int j = 0; j < incrementsPerThread; j++) {
            unsafeCounter[0]++;
            safeCounter.incrementAndGet();
          }
        });
      }
    }

    int expected = numberOfThreads * incrementsPerThread;
    return String.format(
        "Ожидалось: %d | Результат (Race Condition): %d | Результат (Atomic): %d",
        expected, unsafeCounter[0], safeCounter.get()
    );
  }
}
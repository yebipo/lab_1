package com.antiprocrastinate.lab.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ConcurrencyServiceTest {

  private final ConcurrencyService concurrencyService = new ConcurrencyService();

  @Mock
  private ExecutorService mockExecutor;

  @Test
  @DisplayName("1. Успех: стандартное выполнение")
  void testRunRaceConditionDemo_Success() {
    String result = concurrencyService.runRaceConditionDemo(2, 10, 5);
    assertTrue(result.contains("Ожидалось: 20"));
  }

  @Test
  @DisplayName("2. Таймаут: потоки не успели вовремя")
  void testRunRaceConditionDemo_Timeout() {
    String result = concurrencyService.runRaceConditionDemo(10, 100, 0);
    assertEquals("Ошибка: потоки не успели завершить работу за отведенное время (таймаут).", result);
  }

  @Test
  @DisplayName("3. Обработка прерывания (InterruptedException) в главном потоке")
  void testRunRaceConditionDemo_MainThreadInterrupted() {
    try (MockedStatic<Executors> mockedExecutors = Mockito.mockStatic(Executors.class)) {
      mockedExecutors.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(mockExecutor);

      Mockito.doAnswer(invocation -> {
        Thread.currentThread().interrupt();
        return null;
      }).when(mockExecutor).submit(any(Runnable.class));

      String result = concurrencyService.runRaceConditionDemo(2, 10, 5);

      assertEquals("Ошибка: выполнение главного потока было прервано.", result);

      assertTrue(Thread.interrupted(), "Флаг прерывания должен был быть установлен и теперь сброшен");
    }
  }

  @Test
  @DisplayName("4. Обработка прерывания (InterruptedException) в рабочем потоке")
  void testRunRaceConditionDemo_WorkerThreadInterrupted() {
    try (MockedStatic<Executors> mockedExecutors = Mockito.mockStatic(Executors.class)) {
      mockedExecutors.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(mockExecutor);

      Mockito.doAnswer(invocation -> {
        Runnable runnable = invocation.getArgument(0);

        Thread.currentThread().interrupt();
        runnable.run();

        boolean wasInterrupted = Thread.interrupted();
        assertTrue(wasInterrupted);

        return null;
      }).when(mockExecutor).submit(any(Runnable.class));

      String result = concurrencyService.runRaceConditionDemo(2, 10, 5);
      assertTrue(result.contains("Результат (Atomic): 0"));
    }
  }

  @Test
  @DisplayName("5. Успех: вызов метода без параметров (дефолтные значения)")
  void testRunRaceConditionDemo_NoArgs() {
    String result = concurrencyService.runRaceConditionDemo();

    assertTrue(result.contains("Ожидалось: 64000"), "Должно быть 64000 ожидаемых инкрементов");
    assertTrue(result.contains("Результат (Atomic): 64000"), "Atomic результат должен быть точным");
    assertTrue(result.contains("Результат (Unsafe - Race Condition):"), "Должна присутствовать строка с unsafe результатом");
  }
}
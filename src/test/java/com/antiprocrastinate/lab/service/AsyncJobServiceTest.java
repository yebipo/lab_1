package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsyncJobServiceTest {

  private AsyncJobService asyncJobService;

  @BeforeEach
  void setUp() {
    asyncJobService = new AsyncJobService();
  }

  @Test
  void shouldProcessLongTaskSuccessfully() {
    String taskId = "task-1";

    Thread taskThread = new Thread(() -> asyncJobService.processLongTask(taskId, 100L));
    taskThread.start();

    await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(taskId)).isIn("IN_PROGRESS", "COMPLETED")
    );

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(taskId)).isEqualTo("COMPLETED")
    );
  }

  @Test
  void shouldHandleInterruptedException() {
    String taskId = "task-2";

    Thread taskThread = new Thread(() -> asyncJobService.processLongTask(taskId, 5000L));
    taskThread.start();

    await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(taskId)).isEqualTo("IN_PROGRESS")
    );

    taskThread.interrupt();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(taskId)).isEqualTo("FAILED")
    );
  }

  @Test
  void shouldReturnNotFoundForUnknownTask() {
    assertThat(asyncJobService.getStatus("unknown-task")).isEqualTo("NOT_FOUND");
  }
}
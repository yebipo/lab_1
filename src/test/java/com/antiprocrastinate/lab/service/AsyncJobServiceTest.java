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
    String jobId = "job-1";

    Thread taskThread = new Thread(() -> asyncJobService.processLongTask(jobId, 100L));
    taskThread.start();

    await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(jobId)).isIn("IN_PROGRESS", "COMPLETED")
    );

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(jobId)).isEqualTo("COMPLETED")
    );
  }

  @Test
  void shouldHandleInterruptedException() {
    String jobId = "job-2";

    Thread taskThread = new Thread(() -> asyncJobService.processLongTask(jobId, 5000L));
    taskThread.start();

    await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(jobId)).isEqualTo("IN_PROGRESS")
    );

    taskThread.interrupt();

    await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
        assertThat(asyncJobService.getStatus(jobId)).isEqualTo("FAILED")
    );
  }

  @Test
  void shouldReturnNotFoundForUnknownTask() {
    assertThat(asyncJobService.getStatus("unknown-job")).isEqualTo("NOT_FOUND");
  }
}
package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootTest(classes = {
    AsyncBusinessServiceTest.AsyncTestConfig.class,
    AsyncBusinessService.class
})
class AsyncBusinessServiceTest {

  @Configuration
  @EnableAsync
  static class AsyncTestConfig {
  }

  @Autowired
  private AsyncBusinessService asyncBusinessService;

  @Test
  void shouldExecuteAsyncTaskAndCheckStatus() {
    Long expectedTaskId = 99L;
    CompletableFuture<Long> future = asyncBusinessService.processTaskAsync(expectedTaskId);

    assertThat(future.isDone()).isFalse();
    await().atMost(5, TimeUnit.SECONDS).until(future::isDone);

    assertThat(future.isDone()).isTrue();
    assertThat(future.join()).isEqualTo(expectedTaskId);
  }

  @Test
  void shouldHandleInterruption() throws InterruptedException {
    AsyncBusinessService directService = new AsyncBusinessService();

    Thread thread = new Thread(() -> directService.processTaskAsync(1L));
    thread.start();

    await().pollDelay(50, TimeUnit.MILLISECONDS).until(() -> true);

    thread.interrupt();
    thread.join();

    assertThat(thread.isAlive()).isFalse();
  }
}
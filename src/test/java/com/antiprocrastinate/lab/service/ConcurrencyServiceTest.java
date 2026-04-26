package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConcurrencyServiceTest {

  private final ConcurrencyService concurrencyService = new ConcurrencyService();

  @Test
  void shouldDemonstrateRaceCondition() {
    String result = concurrencyService.runRaceConditionDemo();

    assertThat(result)
        .contains("Ожидалось: 64000")
        .contains("Результат (Atomic): 64000")
        .isNotNull();
  }
}
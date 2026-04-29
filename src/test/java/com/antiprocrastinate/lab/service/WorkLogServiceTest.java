package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.WorkLog;
import com.antiprocrastinate.lab.repository.WorkLogRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WorkLogServiceTest {

  @Mock
  private WorkLogRepository workLogRepository;

  @InjectMocks
  private WorkLogService workLogService;

  private WorkLog testWorkLog;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testWorkLog = new WorkLog();
    testWorkLog.setId(1L);
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldFindAll() {
    Page<WorkLog> page = new PageImpl<>(List.of(testWorkLog));
    when(workLogRepository.findAll(pageable)).thenReturn(page);
    Page<WorkLog> result = workLogService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(workLogRepository.findById(1L)).thenReturn(Optional.of(testWorkLog));
    WorkLog result = workLogService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowWhenNotFound() {
    when(workLogRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> workLogService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldSave() {
    when(workLogRepository.save(any(WorkLog.class))).thenReturn(testWorkLog);
    workLogService.save(testWorkLog);
    verify(workLogRepository).save(testWorkLog);
  }

  @Test
  void shouldDeleteById() {
    workLogService.deleteById(1L);
    verify(workLogRepository).deleteById(1L);
  }

  @Test
  void shouldSaveAll() {
    List<WorkLog> logs = List.of(testWorkLog);
    when(workLogRepository.saveAll(logs)).thenReturn(logs);

    List<WorkLog> result = workLogService.saveAll(logs);
    assertThat(result).hasSize(1);
    verify(workLogRepository).saveAll(logs);
  }

  @Test
  void shouldDeleteAll() {
    List<Long> ids = List.of(1L, 2L);
    workLogService.deleteAll(ids);
    verify(workLogRepository).deleteAllByIdInBatch(ids);
  }
}
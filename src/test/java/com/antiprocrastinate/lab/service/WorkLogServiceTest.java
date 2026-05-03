package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.mapper.WorkLogMapper;
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

  @Mock private WorkLogRepository workLogRepository;
  @Mock private WorkLogMapper workLogMapper;
  @InjectMocks private WorkLogService workLogService;

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
    assertThat(workLogService.findAll(pageable).getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(workLogRepository.findById(1L)).thenReturn(Optional.of(testWorkLog));
    assertThat(workLogService.findById(1L).getId()).isEqualTo(1L);
  }

  @Test
  void shouldCreate() {
    WorkLogDto dto = new WorkLogDto();
    when(workLogMapper.toEntity(dto)).thenReturn(testWorkLog);
    when(workLogRepository.save(testWorkLog)).thenReturn(testWorkLog);

    WorkLog result = workLogService.create(dto);
    assertThat(result).isNotNull();
    verify(workLogRepository).save(testWorkLog);
  }

  @Test
  void shouldUpdate() {
    WorkLogDto dto = new WorkLogDto();
    when(workLogRepository.findById(1L)).thenReturn(Optional.of(testWorkLog));
    when(workLogRepository.save(testWorkLog)).thenReturn(testWorkLog);

    workLogService.update(1L, dto);
    verify(workLogMapper).updateEntityFromDto(dto, testWorkLog);
    verify(workLogRepository).save(testWorkLog);
  }

  @Test
  void shouldPatchBulk() {
    WorkLogDto dto = new WorkLogDto(); dto.setId(1L);
    when(workLogRepository.findAllById(List.of(1L))).thenReturn(List.of(testWorkLog));
    when(workLogRepository.saveAll(anyList())).thenReturn(List.of(testWorkLog));

    workLogService.patchBulk(List.of(dto));
    verify(workLogMapper).updateEntityFromDto(dto, testWorkLog);
    verify(workLogRepository).saveAll(anyList());
  }

  @Test
  void shouldDeleteById() {
    workLogService.deleteById(1L);
    verify(workLogRepository).deleteById(1L);
  }

  @Test
  void shouldDeleteAllInBatch() {
    List<Long> ids = List.of(1L, 2L);
    workLogService.deleteAll(ids);
    verify(workLogRepository).deleteAllByIdInBatch(ids);
  }
}
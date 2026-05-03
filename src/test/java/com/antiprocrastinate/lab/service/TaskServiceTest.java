package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.mapper.TaskMapper;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.repository.TaskRepository;
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
class TaskServiceTest {

  @Mock private TaskRepository taskRepository;
  @Mock private TaskMapper taskMapper;
  @InjectMocks private TaskService taskService;

  private Task testTask;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testTask = Task.builder()
        .id(1L)
        .title("Test Task")
        .status(TaskStatus.TODO)
        .build();
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldFindAll() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findAll(pageable)).thenReturn(page);
    assertThat(taskService.findAll(pageable).getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    assertThat(taskService.findById(1L).getId()).isEqualTo(1L);
  }

  @Test
  void shouldCreate() {
    TaskDto dto = new TaskDto();
    when(taskMapper.toEntity(dto)).thenReturn(testTask);
    when(taskRepository.save(testTask)).thenReturn(testTask);

    Task result = taskService.create(dto);
    assertThat(result).isNotNull();
    verify(taskRepository).save(testTask);
  }

  @Test
  void shouldUpdate() {
    TaskDto dto = new TaskDto();
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    when(taskRepository.save(testTask)).thenReturn(testTask);

    taskService.update(1L, dto);
    verify(taskMapper).updateEntityFromDto(dto, testTask);
    verify(taskRepository).save(testTask);
  }

  @Test
  void shouldPatchBulk() {
    TaskDto dto = new TaskDto(); dto.setId(1L);
    when(taskRepository.findAllById(List.of(1L))).thenReturn(List.of(testTask));
    when(taskRepository.saveAll(anyList())).thenReturn(List.of(testTask));

    taskService.patchBulk(List.of(dto));
    verify(taskMapper).updateEntityFromDto(dto, testTask);
    verify(taskRepository).saveAll(anyList());
  }

  @Test
  void shouldDeleteById() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    taskService.deleteById(1L);
    verify(taskRepository).delete(testTask);
  }

  @Test
  void shouldDeleteAll() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    taskService.deleteAll(List.of(1L));
    verify(taskRepository).delete(testTask);
  }

  @Test
  void shouldGetTasksFilteredNative() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findTasksByUserAndSkillNative(1L, 2L, pageable)).thenReturn(page);

    Page<Task> result = taskService.getTasksFiltered(1L, 2L, pageable, true);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldGetTasksFilteredJpql() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findTasksByUserAndSkillJpql(1L, 2L, pageable)).thenReturn(page);

    Page<Task> result = taskService.getTasksFiltered(1L, 2L, pageable, false);
    assertThat(result.getContent()).hasSize(1);
  }
}
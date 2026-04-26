package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private TaskService selfMock;

  private TaskService taskService;
  private Task testTask;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    taskService = new TaskService(taskRepository);
    taskService.setSelf(selfMock);
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
    Page<Task> result = taskService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    assertThat(taskService.findById(1L).getId()).isEqualTo(1L);
  }

  @Test
  void shouldSaveAndInvalidateCache() {
    when(taskRepository.save(any(Task.class))).thenReturn(testTask);
    taskService.save(testTask);
    verify(taskRepository).save(testTask);
  }

  @Test
  void shouldDeleteByIdAndInvalidateCache() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    taskService.deleteById(1L);
    verify(taskRepository).delete(testTask);
  }

  @Test
  void shouldSaveMultipleWithTransaction() {
    Task t2 = Task.builder().id(2L).build();
    taskService.saveMultipleWithTransaction(List.of(testTask, t2));
    verify(taskRepository, times(2)).save(any());
  }

  @Test
  void shouldSaveMultipleWithoutTransactionUsingSelf() {
    Task t2 = Task.builder().id(2L).build();
    taskService.saveMultipleWithoutTransaction(List.of(testTask, t2));
    verify(selfMock).save(testTask);
    verify(selfMock).save(t2);
  }

  @Test
  void shouldCacheFilteredResults() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findTasksByUserAndSkillJpql(1L, 1L, pageable)).thenReturn(page);

    taskService.getTasksFiltered(1L, 1L, pageable, false);
    taskService.getTasksFiltered(1L, 1L, pageable, false);

    verify(taskRepository, times(1)).findTasksByUserAndSkillJpql(1L, 1L, pageable);
  }

  @Test
  void shouldInvalidateCacheOnSaveAction() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findTasksByUserAndSkillJpql(1L, 1L, pageable)).thenReturn(page);
    when(taskRepository.save(any())).thenReturn(testTask);

    taskService.getTasksFiltered(1L, 1L, pageable, false);
    taskService.save(testTask);
    taskService.getTasksFiltered(1L, 1L, pageable, false);

    verify(taskRepository, times(2)).findTasksByUserAndSkillJpql(1L, 1L, pageable);
  }
}
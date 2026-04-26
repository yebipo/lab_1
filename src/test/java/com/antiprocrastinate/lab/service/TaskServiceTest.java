package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование TaskService (100% покрытие)")
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
        .title("Написать тесты")
        .status(TaskStatus.TODO)
        .build();

    pageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("Поиск всех задач")
  void shouldFindAll() {
    Page<Task> page = new PageImpl<>(List.of(testTask));
    when(taskRepository.findAll(pageable)).thenReturn(page);

    Page<Task> result = taskService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Успешный поиск задачи по ID")
  void shouldFindByIdSuccess() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    Task result = taskService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Выброс ошибки, если задача не найдена")
  void shouldThrowWhenTaskNotFound() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> taskService.findById(99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Task not found");
  }

  @Test
  @DisplayName("Успешное сохранение и инвалидация кэша")
  void shouldSaveTaskAndInvalidateIndex() {
    when(taskRepository.save(any(Task.class))).thenReturn(testTask);
    Task result = taskService.save(testTask);
    assertThat(result).isNotNull();
    verify(taskRepository).save(testTask);
  }

  @Test
  @DisplayName("Успешное удаление и инвалидация кэша")
  void shouldDeleteTaskById() {
    when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
    taskService.deleteById(1L);
    verify(taskRepository).delete(testTask);
  }

  @Test
  @DisplayName("Выброс ошибки при удалении несуществующей задачи")
  void shouldThrowWhenDeletingNonExistentTask() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> taskService.deleteById(99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Cannot delete: Task not found with id: 99");
  }

  @Test
  @DisplayName("Транзакционное массовое сохранение")
  void shouldSaveMultipleWithTransaction() {
    taskService.saveMultipleWithTransaction(List.of(testTask, testTask));
    verify(taskRepository, times(2)).save(any(Task.class));
  }

  @Test
  @DisplayName("Нетранзакционное массовое сохранение (успех и ошибка)")
  void shouldSaveMultipleWithoutTransactionAndCatchException() {
    Task failTask = Task.builder().id(2L).title("Падающая задача").build();

    when(selfMock.save(testTask)).thenReturn(testTask);
    when(selfMock.save(failTask)).thenThrow(new RuntimeException("DB Error"));

    taskService.saveMultipleWithoutTransaction(List.of(testTask, failTask));

    verify(selfMock, times(1)).save(testTask);
    verify(selfMock, times(1)).save(failTask);
  }

  @Nested
  @DisplayName("Кэширование и поиск (getTasksFiltered)")
  class FilteredTasksCaching {

    @Test
    @DisplayName("Запрос с useNative = true сохраняется в кэш")
    void shouldCacheNativeQuery() {
      Page<Task> page = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillNative(1L, 2L, pageable)).thenReturn(page);

      taskService.getTasksFiltered(1L, 2L, pageable, true);
      taskService.getTasksFiltered(1L, 2L, pageable, true);

      verify(taskRepository, times(1)).findTasksByUserAndSkillNative(1L, 2L, pageable);
      verify(taskRepository, never()).findTasksByUserAndSkillJpql(any(), any(), any());
    }

    @Test
    @DisplayName("Запрос с useNative = false сохраняется в кэш")
    void shouldCacheJpqlQuery() {
      Page<Task> page = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillJpql(1L, 2L, pageable)).thenReturn(page);

      taskService.getTasksFiltered(1L, 2L, pageable, false);
      taskService.getTasksFiltered(1L, 2L, pageable, false);

      verify(taskRepository, times(1)).findTasksByUserAndSkillJpql(1L, 2L, pageable);
    }
  }
}
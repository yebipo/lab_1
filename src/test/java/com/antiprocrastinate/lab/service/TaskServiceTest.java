package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.Collections;
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
@DisplayName("Тестирование TaskService")
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private TaskService selfMock;

  private TaskService taskService;
  private Task testTask;
  private Pageable defaultPageable;

  @BeforeEach
  void setUp() {
    taskService = new TaskService(taskRepository);
    taskService.setSelf(selfMock);

    testTask = Task.builder()
        .id(1L)
        .title("Test Task")
        .description("Test Description")
        .focusScore(10)
        .status(TaskStatus.TODO)
        .build();

    defaultPageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("Конструктор должен обрабатывать null")
  void constructorShouldCheckNull() {
    assertThatThrownBy(() -> new TaskService(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Nested
  @DisplayName("Операции поиска (Find)")
  class FindOperations {

    @Test
    @DisplayName("Должен находить все задачи с учетом пагинации")
    void shouldFindAllTasks() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findAll(defaultPageable)).thenReturn(taskPage);

      Page<Task> result = taskService.findAll(defaultPageable);

      assertThat(result).isNotNull();
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Task");
      verify(taskRepository).findAll(defaultPageable);
    }

    @Test
    @DisplayName("Должен успешно находить задачу по её ID")
    void shouldFindTaskById() {
      when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

      Task result = taskService.findById(1L);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение ResourceNotFoundException, если задача не найдена")
    void shouldThrowExceptionWhenTaskNotFound() {
      when(taskRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> taskService.findById(999L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Task not found with id: 999");
    }
  }

  @Nested
  @DisplayName("Операции сохранения и удаления (Save & Delete)")
  class SaveAndDeleteOperations {

    @Test
    @DisplayName("Должен успешно сохранять задачу и очищать кэш индексов")
    void shouldSaveTask() {
      when(taskRepository.save(any(Task.class))).thenReturn(testTask);

      Task result = taskService.save(testTask);

      assertThat(result).isNotNull();
      assertThat(result.getTitle()).isEqualTo("Test Task");
      verify(taskRepository).save(testTask);
    }

    @Test
    @DisplayName("Должен успешно удалять задачу по ID и очищать кэш индексов")
    void shouldDeleteTaskById() {
      when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

      taskService.deleteById(1L);

      verify(taskRepository).findById(1L);
      verify(taskRepository).delete(testTask);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при попытке удалить несуществующую задачу")
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
      when(taskRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> taskService.deleteById(999L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Cannot delete: Task not found with id: 999");

      verify(taskRepository, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("Пакетные операции (Bulk Operations)")
  class BulkOperations {

    @Test
    @DisplayName("Должен сохранять список задач в рамках транзакции")
    void shouldSaveMultipleTasksWithTransaction() {
      Task task2 = Task.builder().id(2L).build();
      List<Task> tasks = List.of(testTask, task2);

      when(taskRepository.save(any(Task.class))).thenReturn(testTask);

      taskService.saveMultipleWithTransaction(tasks);

      verify(taskRepository, times(2)).save(any(Task.class));
    }

    @Test
    @DisplayName("Должен сохранять список задач без общей транзакции")
    void shouldSaveMultipleTasksWithoutTransaction() {
      Task task2 = Task.builder().id(2L).build();
      List<Task> tasks = List.of(testTask, task2);

      taskService.saveMultipleWithoutTransaction(tasks);

      verify(selfMock, times(1)).save(testTask);
      verify(selfMock, times(1)).save(task2);
    }

    @Test
    @DisplayName("Не должен прерывать сохранение остальных задач, если одна из них упала")
    void shouldContinueSavingWhenOneFailsWithoutTransaction() {
      Task task2 = Task.builder().id(2L).build();
      List<Task> tasks = List.of(testTask, task2);

      doThrow(new RuntimeException("Save error")).when(selfMock).save(testTask);

      taskService.saveMultipleWithoutTransaction(tasks);

      verify(selfMock).save(testTask);
      verify(selfMock).save(task2);
    }

    @Test
    @DisplayName("Должен обрабатывать пустые списки без ошибок")
    void shouldHandleEmptyLists() {
      taskService.saveMultipleWithTransaction(Collections.emptyList());
      verify(taskRepository, never()).save(any());

      taskService.saveMultipleWithoutTransaction(Collections.emptyList());
      verify(selfMock, never()).save(any());
    }

    @Test
    @DisplayName("Должен обрабатывать null списки")
    @SuppressWarnings("ConstantConditions")
    void shouldHandleNullLists() {
      assertThatThrownBy(() -> taskService.saveMultipleWithTransaction(null))
          .isInstanceOf(NullPointerException.class);

      assertThatThrownBy(() -> taskService.saveMultipleWithoutTransaction(null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("Фильтрация задач и поведение кэширования")
  class FilteredTasksAndCaching {

    @Test
    @DisplayName("Должен использовать нативный запрос")
    void shouldUseNativeQuery() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillNative(1L, 2L, defaultPageable)).thenReturn(taskPage);

      Page<Task> result = taskService.getTasksFiltered(1L, 2L, defaultPageable, true);

      assertThat(result.getContent()).hasSize(1);
      verify(taskRepository).findTasksByUserAndSkillNative(1L, 2L, defaultPageable);
      verify(taskRepository, never()).findTasksByUserAndSkillJpql(any(), any(), any());
    }

    @Test
    @DisplayName("Должен использовать JPQL запрос")
    void shouldUseJpqlQuery() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillJpql(1L, 2L, defaultPageable)).thenReturn(taskPage);

      Page<Task> result = taskService.getTasksFiltered(1L, 2L, defaultPageable, false);

      assertThat(result.getContent()).hasSize(1);
      verify(taskRepository).findTasksByUserAndSkillJpql(1L, 2L, defaultPageable);
      verify(taskRepository, never()).findTasksByUserAndSkillNative(any(), any(), any());
    }

    @Test
    @DisplayName("Должен возвращать результаты из кэша при повторных JPQL запросах")
    void shouldReturnFromCacheJpql() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillJpql(1L, 2L, defaultPageable)).thenReturn(taskPage);

      taskService.getTasksFiltered(1L, 2L, defaultPageable, false);
      taskService.getTasksFiltered(1L, 2L, defaultPageable, false);

      verify(taskRepository, times(1)).findTasksByUserAndSkillJpql(1L, 2L, defaultPageable);
    }

    @Test
    @DisplayName("Должен возвращать результаты из кэша при повторных Native запросах")
    void shouldReturnFromCacheNative() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillNative(1L, 2L, defaultPageable)).thenReturn(taskPage);

      taskService.getTasksFiltered(1L, 2L, defaultPageable, true);
      taskService.getTasksFiltered(1L, 2L, defaultPageable, true);

      verify(taskRepository, times(1)).findTasksByUserAndSkillNative(1L, 2L, defaultPageable);
    }

    @Test
    @DisplayName("Должен инвалидировать кэш при сохранении новой задачи")
    void shouldInvalidateCacheOnSave() {
      Page<Task> taskPage = new PageImpl<>(List.of(testTask));
      when(taskRepository.findTasksByUserAndSkillJpql(1L, 2L, defaultPageable)).thenReturn(taskPage);
      when(taskRepository.save(any(Task.class))).thenReturn(testTask);

      taskService.getTasksFiltered(1L, 2L, defaultPageable, false);
      taskService.save(testTask);
      taskService.getTasksFiltered(1L, 2L, defaultPageable, false);

      verify(taskRepository, times(2)).findTasksByUserAndSkillJpql(1L, 2L, defaultPageable);
    }
  }
}
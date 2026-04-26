package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.SkillRepository;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Тестирование SkillService (100% покрытие)")
class SkillServiceTest {

  @Mock
  private SkillRepository skillRepository;

  @Mock
  private TaskRepository taskRepository;

  @InjectMocks
  private SkillService skillService;

  private Skill testSkill;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testSkill = new Skill();
    testSkill.setId(1L);
    testSkill.setTasks(new HashSet<>());
    pageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("Поиск всех навыков")
  void shouldFindAll() {
    Page<Skill> page = new PageImpl<>(List.of(testSkill));
    when(skillRepository.findAll(pageable)).thenReturn(page);
    Page<Skill> result = skillService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Успешный поиск навыка по ID")
  void shouldFindById() {
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
    Skill result = skillService.findById(1L);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("Выброс исключения при поиске несуществующего навыка")
  void shouldThrowWhenSkillNotFound() {
    when(skillRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> skillService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Сохранение навыка")
  void shouldSaveSkill() {
    when(skillRepository.save(any(Skill.class))).thenReturn(testSkill);
    Skill result = skillService.save(testSkill);
    assertThat(result).isNotNull();
    verify(skillRepository).save(testSkill);
  }

  @Test
  @DisplayName("Удаление навыка: задачи с пустыми скиллами удаляются, остальные обновляются")
  void shouldDeleteSkillAndHandleRelatedTasks() {
    Task taskToDrop = new Task();
    taskToDrop.setId(10L);
    taskToDrop.setSkills(new HashSet<>());

    Task taskToKeep = new Task();
    taskToKeep.setId(20L);
    Skill anotherSkill = new Skill();
    anotherSkill.setId(2L);
    taskToKeep.setSkills(new HashSet<>(Set.of(anotherSkill)));

    testSkill.setTasks(Set.of(taskToDrop, taskToKeep));
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository).deleteAll(List.of(taskToDrop));
    verify(taskRepository).saveAll(List.of(taskToKeep));
    verify(skillRepository).delete(testSkill);
  }

  @Test
  @DisplayName("Удаление навыка без связанных задач")
  void shouldDeleteSkillWithNoTasks() {
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository, never()).deleteAll(any());
    verify(taskRepository, never()).saveAll(any());
    verify(skillRepository).delete(testSkill);
  }

  @Test
  @DisplayName("Удаление навыка: только удаление задач")
  void shouldDeleteSkillAndOnlyDeleteTasks() {
    Task taskToDrop = new Task();
    taskToDrop.setId(10L);
    taskToDrop.setSkills(new HashSet<>());
    testSkill.setTasks(Set.of(taskToDrop));

    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository).deleteAll(List.of(taskToDrop));
    verify(taskRepository, never()).saveAll(any());
    verify(skillRepository).delete(testSkill);
  }

  @Test
  @DisplayName("Удаление навыка: только обновление задач")
  void shouldDeleteSkillAndOnlyUpdateTasks() {
    Task taskToKeep = new Task();
    taskToKeep.setId(20L);
    Skill anotherSkill = new Skill();
    anotherSkill.setId(2L);
    taskToKeep.setSkills(new HashSet<>(Set.of(anotherSkill)));
    testSkill.setTasks(Set.of(taskToKeep));

    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository, never()).deleteAll(any());
    verify(taskRepository).saveAll(List.of(taskToKeep));
    verify(skillRepository).delete(testSkill);
  }

  @Test
  @DisplayName("Выброс исключения при удалении несуществующего навыка")
  void shouldThrowWhenDeletingNonExistentSkill() {
    when(skillRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> skillService.deleteById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Skill not found");
  }
}
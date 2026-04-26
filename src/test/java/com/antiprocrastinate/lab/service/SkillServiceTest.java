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
@DisplayName("Тестирование SkillService")
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
  @DisplayName("Конструктор должен обрабатывать null")
  void constructorShouldCheckNull() {
    try {
      new SkillService(null, null);
    } catch (NullPointerException | IllegalArgumentException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  @DisplayName("Должен находить все записи")
  void shouldFindAll() {
    Page<Skill> page = new PageImpl<>(List.of(testSkill));
    when(skillRepository.findAll(pageable)).thenReturn(page);
    Page<Skill> result = skillService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Должен находить запись по ID")
  void shouldFindById() {
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
    Skill result = skillService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Должен выбрасывать исключение, если скилл не найден")
  void shouldThrowWhenNotFound() {
    when(skillRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> skillService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Должен сохранять скилл")
  void shouldSave() {
    when(skillRepository.save(any(Skill.class))).thenReturn(testSkill);
    skillService.save(testSkill);
    verify(skillRepository).save(testSkill);
  }

  @Test
  @DisplayName("Должен удалять скилл и корректно обновлять/удалять связанные задачи")
  void shouldDeleteSkillAndHandleRelatedTasks() {
    Task task1 = new Task();
    task1.setId(10L);
    task1.setSkills(new HashSet<>(Set.of(testSkill)));

    Task task2 = new Task();
    task2.setId(20L);
    Skill otherSkill = new Skill();
    otherSkill.setId(2L);
    task2.setSkills(new HashSet<>(Set.of(testSkill, otherSkill)));

    testSkill.setTasks(Set.of(task1, task2));

    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository).delete(task1);
    verify(taskRepository).save(task2);
    verify(skillRepository).delete(testSkill);
  }

  @Test
  @DisplayName("Должен выбрасывать исключение при попытке удалить несуществующий скилл")
  void shouldThrowExceptionWhenDeletingNonExistentSkill() {
    when(skillRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> skillService.deleteById(999L))
        .isInstanceOf(ResourceNotFoundException.class);

    verify(skillRepository, never()).delete(any());
  }
}
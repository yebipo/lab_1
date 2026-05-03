package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.mapper.SkillMapper;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.SkillRepository;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
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
class SkillServiceTest {

  @Mock private SkillRepository skillRepository;
  @Mock private TaskRepository taskRepository;
  @Mock private SkillMapper skillMapper;
  @InjectMocks private SkillService skillService;

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
  void shouldFindAll() {
    Page<Skill> page = new PageImpl<>(List.of(testSkill));
    when(skillRepository.findAll(pageable)).thenReturn(page);
    assertThat(skillService.findAll(pageable).getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
    assertThat(skillService.findById(1L).getId()).isEqualTo(1L);
  }

  @Test
  void shouldCreate() {
    SkillDto dto = new SkillDto();
    when(skillMapper.toEntity(dto)).thenReturn(testSkill);
    when(skillRepository.save(testSkill)).thenReturn(testSkill);

    Skill result = skillService.create(dto);
    assertThat(result).isNotNull();
    verify(skillRepository).save(testSkill);
  }

  @Test
  void shouldUpdate() {
    SkillDto dto = new SkillDto();
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
    when(skillRepository.save(testSkill)).thenReturn(testSkill);

    skillService.update(1L, dto);
    verify(skillMapper).updateEntityFromDto(dto, testSkill);
    verify(skillRepository).save(testSkill);
  }

  @Test
  void shouldPatchBulk() {
    SkillDto dto = new SkillDto(); dto.setId(1L);
    when(skillRepository.findAllById(List.of(1L))).thenReturn(List.of(testSkill));
    when(skillRepository.saveAll(anyList())).thenReturn(List.of(testSkill));

    skillService.patchBulk(List.of(dto));
    verify(skillMapper).updateEntityFromDto(dto, testSkill);
    verify(skillRepository).saveAll(anyList());
  }

  @Test
  void shouldDeleteSkillAndPartitionTasks() {
    Task taskEmpty = new Task(); taskEmpty.setId(10L);
    taskEmpty.setSkills(new HashSet<>(List.of(testSkill))); // Останется без навыков

    Task taskNotEmpty = new Task(); taskNotEmpty.setId(20L);
    Skill otherSkill = new Skill(); otherSkill.setId(2L);
    taskNotEmpty.setSkills(new HashSet<>(List.of(testSkill, otherSkill))); // Навык останется

    testSkill.setTasks(new HashSet<>(List.of(taskEmpty, taskNotEmpty)));
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));

    skillService.deleteById(1L);

    verify(taskRepository).deleteAll(List.of(taskEmpty));
    verify(taskRepository).saveAll(List.of(taskNotEmpty));
    verify(skillRepository).delete(testSkill);
  }

  @Test
  void shouldDeleteAll() {
    when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
    skillService.deleteAll(List.of(1L));
    verify(skillRepository).delete(testSkill);
  }
}
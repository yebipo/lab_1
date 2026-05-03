package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.SkillMapper;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.SkillRepository;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;
  private final TaskRepository taskRepository;
  private final SkillMapper skillMapper;

  @Transactional(readOnly = true)
  public Page<Skill> findAll(Pageable pageable) {
    return skillRepository.findAll(pageable);
  }

  @Cacheable(value = "skill_item", key = "#id")
  @Transactional(readOnly = true)
  public Skill findById(Long id) {
    return skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
  }

  @CachePut(value = "skill_item", key = "#result.id")
  @Transactional
  public Skill save(Skill skill) {
    return skillRepository.save(skill);
  }

  @CacheEvict(value = "skill_item", allEntries = true)
  @Transactional
  public List<Skill> saveAll(List<Skill> skills) {
    return skillRepository.saveAll(skills);
  }

  @CacheEvict(value = "skill_item", allEntries = true)
  @Transactional
  public List<Skill> patchBulk(List<SkillDto> dtos) {
    List<Long> ids = dtos.stream().map(SkillDto::getId).toList();
    List<Skill> existingSkills = skillRepository.findAllById(ids);

    if (existingSkills.size() != ids.size()) {
      throw new ResourceNotFoundException("Один или несколько навыков не найдены");
    }

    Map<Long, SkillDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(SkillDto::getId, dto -> dto));

    existingSkills.forEach(existing -> {
      SkillDto dto = dtoMap.get(existing.getId());
      skillMapper.updateEntityFromDto(dto, existing);
    });

    return skillRepository.saveAll(existingSkills);
  }

  @CacheEvict(value = "skill_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    Skill skill = skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

    skill.getTasks().forEach(task -> task.getSkills().remove(skill));

    Map<Boolean, List<Task>> partitionedTasks = skill.getTasks().stream()
        .collect(Collectors.partitioningBy(task -> task.getSkills().isEmpty()));

    if (!partitionedTasks.get(true).isEmpty()) {
      taskRepository.deleteAll(partitionedTasks.get(true));
    }

    if (!partitionedTasks.get(false).isEmpty()) {
      taskRepository.saveAll(partitionedTasks.get(false));
    }

    skillRepository.delete(skill);
  }

  @CacheEvict(value = "skill_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteById);
  }
}
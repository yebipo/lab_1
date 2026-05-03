package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
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
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;
  private final TaskRepository taskRepository;

  @Cacheable(value = "skills_page", key = "{#pageable.pageNumber, #pageable.pageSize}")
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

  @Caching(
      put = { @CachePut(value = "skill_item", key = "#result.id") },
      evict = { @CacheEvict(value = "skills_page", allEntries = true) }
  )
  @Transactional
  public Skill save(Skill skill) {
    return skillRepository.save(skill);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "skill_item", allEntries = true),
          @CacheEvict(value = "skills_page", allEntries = true)
      }
  )
  @Transactional
  public List<Skill> saveAll(List<Skill> skills) {
    return skillRepository.saveAll(skills);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "skill_item", allEntries = true),
          @CacheEvict(value = "skills_page", allEntries = true)
      }
  )
  @Transactional
  public List<Skill> patchBulk(List<SkillDto> dtos) {
    List<Long> ids = dtos.stream().map(SkillDto::getId).toList();
    List<Skill> existingSkills = skillRepository.findAllById(ids);

    if (existingSkills.size() != ids.size()) {
      throw new ResourceNotFoundException("Один или несколько навыков не найдены");
    }

    Map<Long, SkillDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(
            SkillDto::getId, dto -> dto, (existing, replacement) -> replacement));

    existingSkills.forEach(existing -> {
      SkillDto dto = dtoMap.get(existing.getId());
      if (dto.getName() != null) {
        existing.setName(dto.getName());
      }
      if (dto.getIconUrl() != null) {
        existing.setIconUrl(dto.getIconUrl());
      }
    });

    return skillRepository.saveAll(existingSkills);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "skill_item", key = "#id"),
          @CacheEvict(value = "skills_page", allEntries = true)
      }
  )
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

  @Caching(
      evict = {
          @CacheEvict(value = "skill_item", allEntries = true),
          @CacheEvict(value = "skills_page", allEntries = true)
      }
  )
  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteById);
  }
}
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
  public Skill create(SkillDto dto) {
    return skillRepository.save(skillMapper.toEntity(dto));
  }

  @CachePut(value = "skill_item", key = "#id")
  @Transactional
  public Skill update(Long id, SkillDto dto) {
    Skill existing = skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    skillMapper.updateEntityFromDto(dto, existing);
    return skillRepository.save(existing);
  }

  @CacheEvict(value = "skill_item", allEntries = true)
  @Transactional
  public List<Skill> patchBulk(List<SkillDto> dtos) {
    List<Long> ids = dtos.stream().map(SkillDto::getId).toList();
    List<Skill> entities = skillRepository.findAllById(ids);
    if (entities.size() != ids.size()) {
      throw new ResourceNotFoundException("One or more skills not found");
    }
    Map<Long, SkillDto> dtoMap = dtos.stream().collect(Collectors.toMap(SkillDto::getId, d -> d));
    entities.forEach(e -> skillMapper.updateEntityFromDto(dtoMap.get(e.getId()), e));
    return skillRepository.saveAll(entities);
  }

  @CacheEvict(value = "skill_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    deleteInternal(id);
  }

  @CacheEvict(value = "skill_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    for (Long id : ids) {
      deleteInternal(id);
    }
  }

  private void deleteInternal(Long id) {
    Skill skill = skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    skill.getTasks().forEach(t -> t.getSkills().remove(skill));
    Map<Boolean, List<Task>> partitioned = skill.getTasks().stream()
        .collect(Collectors.partitioningBy(t -> t.getSkills().isEmpty()));
    if (!partitioned.get(true).isEmpty()) {
      taskRepository.deleteAll(partitioned.get(true));
    }
    if (!partitioned.get(false).isEmpty()) {
      taskRepository.saveAll(partitioned.get(false));
    }
    skillRepository.delete(skill);
  }
}
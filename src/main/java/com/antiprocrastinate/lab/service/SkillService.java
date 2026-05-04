package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.SkillCreateDto;
import com.antiprocrastinate.lab.dto.SkillResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.SkillMapper;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;
  private final SkillMapper skillMapper;

  @Transactional(readOnly = true)
  public Page<SkillResponseDto> findAll(Pageable pageable) {
    return skillRepository.findAll(pageable).map(skillMapper::toResponseDto);
  }

  @Cacheable(value = "skill_item", key = "#id")
  @Transactional(readOnly = true)
  public SkillResponseDto findById(Long id) {
    return skillRepository.findById(id)
        .map(skillMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
  }

  @Transactional
  public SkillResponseDto create(SkillCreateDto dto) {
    return skillMapper.toResponseDto(skillRepository.save(skillMapper.toEntity(dto)));
  }

  @CacheEvict(value = "skill_item", key = "#id")
  @Transactional
  public SkillResponseDto update(Long id, SkillCreateDto dto) {
    Skill existing = skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
    skillMapper.updateEntity(dto, existing);
    return skillMapper.toResponseDto(skillRepository.save(existing));
  }

  @CacheEvict(value = "skill_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    skillRepository.deleteById(id);
  }
}
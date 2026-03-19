package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
  public SkillDto toDto(Skill skill) {
    if (skill == null) {
      return null;
    }
    SkillDto dto = new SkillDto();
    dto.setId(skill.getId());
    dto.setName(skill.getName());
    dto.setIconUrl(skill.getIconUrl());
    if (skill.getCategory() != null) {
      dto.setCategoryId(skill.getCategory().getId());
    }
    return dto;
  }
}
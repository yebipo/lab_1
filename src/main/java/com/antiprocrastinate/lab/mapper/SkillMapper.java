package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
  public SkillDto toDto(Skill skill) {
    SkillDto dto = new SkillDto();
    dto.setId(skill.getId());
    dto.setName(skill.getName());
    dto.setIconUrl(skill.getIconUrl());
    if (skill.getCategory() != null) {
      dto.setCategoryId(skill.getCategory().getId());
    }
    if (skill.getUser() != null) {
      dto.setUserId(skill.getUser().getId());
    }
    return dto;
  }
}
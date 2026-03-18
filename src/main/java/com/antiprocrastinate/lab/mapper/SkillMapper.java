package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
  public SkillDto toDto(Skill skill) {
    SkillDto dto = new SkillDto();
    dto.setName(skill.getName());
    dto.setIconUrl(skill.getIconUrl());
    Category category = skill.getCategory();
    if (category != null) {
      dto.setCategoryId(category.getId());
      dto.setCategoryName(category.getName());
      dto.setCategoryColor(category.getColor());
    }
    return dto;
  }
}
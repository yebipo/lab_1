package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
  public SkillDto toDto(Skill skill) {
    if (skill == null) {
      return null;
    }
    SkillDto dto = new SkillDto();
    BeanUtils.copyProperties(skill, dto);
    if (skill.getCategory() != null) {
      dto.setCategoryId(skill.getCategory().getId());
    }
    return dto;
  }

  public Skill toEntity(SkillDto dto) {
    if (dto == null) {
      return null;
    }
    Skill skill = new Skill();
    BeanUtils.copyProperties(dto, skill);
    if (dto.getCategoryId() != null) {
      Category category = new Category();
      category.setId(dto.getCategoryId());
      skill.setCategory(category);
    }
    return skill;
  }
}
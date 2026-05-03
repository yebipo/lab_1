package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillMapper {
  @Mapping(source = "category.id", target = "categoryId")
  SkillDto toDto(Skill skill);

  @Mapping(source = "categoryId", target = "category.id")
  Skill toEntity(SkillDto dto);
}
package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillCreateDto;
import com.antiprocrastinate.lab.dto.SkillResponseDto;
import com.antiprocrastinate.lab.model.Skill;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SkillMapper {
  @Mapping(source = "category.id", target = "categoryId")
  SkillResponseDto toResponseDto(Skill skill);

  @Mapping(source = "categoryId", target = "category.id")
  Skill toEntity(SkillCreateDto dto);

  @Mapping(source = "categoryId", target = "category.id")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(SkillCreateDto dto, @MappingTarget Skill skill);
}
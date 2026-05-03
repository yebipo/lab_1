package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.model.Skill;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SkillMapper {

  @Mapping(source = "category.id", target = "categoryId")
  SkillDto toDto(Skill skill);

  @Mapping(source = "categoryId", target = "category.id")
  Skill toEntity(SkillDto dto);

  @Mapping(source = "categoryId", target = "category.id",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(SkillDto dto, @MappingTarget Skill skill);
}
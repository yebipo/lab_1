package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryDto toDto(Category category);

  Category toEntity(CategoryDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(CategoryDto dto, @MappingTarget Category category);
}
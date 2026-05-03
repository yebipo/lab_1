package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.CategoryCreateDto;
import com.antiprocrastinate.lab.dto.CategoryResponseDto;
import com.antiprocrastinate.lab.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryResponseDto toResponseDto(Category category);

  Category toEntity(CategoryCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(CategoryCreateDto dto, @MappingTarget Category category);
}
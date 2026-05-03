package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryDto toDto(Category category);

  Category toEntity(CategoryDto dto);
}
package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.model.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
  public CategoryDto toDto(Category category) {
    if (category == null) {
      return null;
    }
    CategoryDto dto = new CategoryDto();
    BeanUtils.copyProperties(category, dto);
    return dto;
  }

  public Category toEntity(CategoryDto dto) {
    if (dto == null) {
      return null;
    }
    Category category = new Category();
    BeanUtils.copyProperties(dto, category);
    return category;
  }
}
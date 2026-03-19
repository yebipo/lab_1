package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
  public CategoryDto toDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setColor(category.getColor());
    dto.setDescription(category.getDescription());
    dto.setIconUrl(category.getIconUrl());
    return dto;
  }
}
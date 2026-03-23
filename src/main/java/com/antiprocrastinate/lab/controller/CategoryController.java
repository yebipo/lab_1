package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.mapper.CategoryMapper;
import com.antiprocrastinate.lab.service.CategoryService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @GetMapping
  public Set<CategoryDto> getAll() {
    return categoryService.findAll().stream()
        .map(categoryMapper::toDto)
        .collect(Collectors.toSet());
  }

  @PostMapping
  public CategoryDto create(@RequestBody CategoryDto categoryDto) {
    return categoryMapper.toDto(categoryService.save(categoryMapper.toEntity(categoryDto)));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    categoryService.deleteById(id);
  }
}
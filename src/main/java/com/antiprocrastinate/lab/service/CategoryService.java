package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.CategoryCreateDto;
import com.antiprocrastinate.lab.dto.CategoryResponseDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.CategoryMapper;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public Page<CategoryResponseDto> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable).map(categoryMapper::toResponseDto);
  }

  @Cacheable(value = "category_item", key = "#id")
  @Transactional(readOnly = true)
  public CategoryResponseDto findById(Long id) {
    return categoryRepository.findById(id)
        .map(categoryMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public CategoryResponseDto create(CategoryCreateDto dto) {
    return categoryMapper.toResponseDto(categoryRepository.save(categoryMapper.toEntity(dto)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @CacheEvict(value = "category_item", key = "#id")
  @Transactional
  public CategoryResponseDto update(Long id, CategoryCreateDto dto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    categoryMapper.updateEntity(dto, existing);
    return categoryMapper.toResponseDto(categoryRepository.save(existing));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @CacheEvict(value = "category_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }
}
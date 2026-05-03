package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final SkillService skillService;

  @Cacheable(value = "categories", key = "{#pageable.pageNumber, #pageable.pageSize}")
  @Transactional(readOnly = true)
  public Page<Category> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Cacheable(value = "categories", key = "#id")
  @Transactional(readOnly = true)
  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public Category save(Category category) {
    return categoryRepository.save(category);
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public List<Category> saveAll(List<Category> categories) {
    return categoryRepository.saveAll(categories);
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public List<Category> patchBulk(List<CategoryDto> dtos) {
    List<Long> ids = dtos.stream().map(CategoryDto::getId).toList();
    List<Category> existingCategories = categoryRepository.findAllById(ids);

    if (existingCategories.size() != ids.size()) {
      throw new ResourceNotFoundException("Одна или несколько категорий не найдены");
    }

    Map<Long, CategoryDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(
            CategoryDto::getId, dto -> dto, (existing, replacement) -> replacement));

    existingCategories.forEach(existing -> {
      CategoryDto dto = dtoMap.get(existing.getId());
      if (dto.getName() != null) {
        existing.setName(dto.getName());
      }
      if (dto.getColor() != null) {
        existing.setColor(dto.getColor());
      }
      if (dto.getDescription() != null) {
        existing.setDescription(dto.getDescription());
      }
      if (dto.getIconUrl() != null) {
        existing.setIconUrl(dto.getIconUrl());
      }
    });

    return categoryRepository.saveAll(existingCategories);
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public void deleteById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    category.getSkills().stream()
        .map(Skill::getId)
        .forEach(skillService::deleteById);

    categoryRepository.delete(category);
  }

  @CacheEvict(value = "categories", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    ids.forEach(this::deleteById);
  }
}
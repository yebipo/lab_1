package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.CategoryMapper;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public Page<Category> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Cacheable(value = "category_item", key = "#id")
  @Transactional(readOnly = true)
  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
  }

  @CachePut(value = "category_item", key = "#result.id")
  @Transactional
  public Category create(CategoryDto dto) {
    Category category = categoryMapper.toEntity(dto);
    return categoryRepository.save(category);
  }

  @CachePut(value = "category_item", key = "#id")
  @Transactional
  public Category update(Long id, CategoryDto dto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    categoryMapper.updateEntityFromDto(dto, existing);
    return categoryRepository.save(existing);
  }

  @CacheEvict(value = "category_item", allEntries = true)
  @Transactional
  public List<Category> patchBulk(List<CategoryDto> dtos) {
    List<Long> ids = dtos.stream().map(CategoryDto::getId).toList();
    List<Category> existingCategories = categoryRepository.findAllById(ids);

    if (existingCategories.size() != ids.size()) {
      throw new ResourceNotFoundException("Одна или несколько категорий не найдены");
    }

    Map<Long, CategoryDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(CategoryDto::getId, dto -> dto));

    existingCategories.forEach(existing -> {
      CategoryDto dto = dtoMap.get(existing.getId());
      categoryMapper.updateEntityFromDto(dto, existing);
    });

    return categoryRepository.saveAll(existingCategories);
  }

  @CacheEvict(value = "category_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    deleteInternal(id);
  }

  @CacheEvict(value = "category_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    for (Long id : ids) {
      deleteInternal(id);
    }
  }

  private void deleteInternal(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    category.getSkills().stream()
        .map(Skill::getId)
        .forEach(skillService::deleteById);

    categoryRepository.delete(category);
  }
}
package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final SkillService skillService;

  @Transactional(readOnly = true)
  public Set<Category> findAll() {
    return new HashSet<>(categoryRepository.findAll());
  }

  @Transactional(readOnly = true)
  public Category findById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
  }

  @Transactional
  public Category save(Category category) {
    return categoryRepository.save(category);
  }

  @Transactional
  public void deleteById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

    Set<Skill> skills = new HashSet<>(category.getSkills());
    for (Skill skill : skills) {
      skillService.deleteById(skill.getId());
    }

    categoryRepository.delete(category);
  }
}
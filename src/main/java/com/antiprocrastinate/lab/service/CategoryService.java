package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public Set<Category> findAll() {
    return new HashSet<>(categoryRepository.findAll());
  }

  public Category save(Category category) {
    return categoryRepository.save(category);
  }

  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }
}
package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.CategoryRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private SkillService skillService;

  @InjectMocks
  private CategoryService categoryService;

  private Category testCategory;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setSkills(new HashSet<>());
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldFindAll() {
    Page<Category> page = new PageImpl<>(List.of(testCategory));
    when(categoryRepository.findAll(pageable)).thenReturn(page);
    Page<Category> result = categoryService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
    Category result = categoryService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowWhenNotFound() {
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldSave() {
    when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
    Category result = categoryService.save(testCategory);
    assertThat(result).isNotNull();
    verify(categoryRepository).save(testCategory);
  }

  @Test
  void shouldDeleteCategoryAndRelatedSkills() {
    Skill skill1 = new Skill();
    skill1.setId(10L);
    Skill skill2 = new Skill();
    skill2.setId(20L);
    testCategory.setSkills(Set.of(skill1, skill2));

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

    categoryService.deleteById(1L);

    verify(skillService).deleteById(10L);
    verify(skillService).deleteById(20L);
    verify(categoryRepository).delete(testCategory);
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistentCategory() {
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.deleteById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  void shouldSaveAll() {
    List<Category> categories = List.of(testCategory);
    when(categoryRepository.saveAll(categories)).thenReturn(categories);

    List<Category> result = categoryService.saveAll(categories);
    assertThat(result).hasSize(1);
    verify(categoryRepository).saveAll(categories);
  }

  @Test
  void shouldDeleteAll() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
    when(categoryRepository.findById(2L)).thenReturn(Optional.of(testCategory));

    categoryService.deleteAll(List.of(1L, 2L));

    verify(categoryRepository, times(2)).delete(testCategory);
  }
}
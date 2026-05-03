package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.CategoryMapper;
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

  @Mock private CategoryRepository categoryRepository;
  @Mock private SkillService skillService;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private CategoryService categoryService;

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
  void shouldCreate() {
    CategoryDto dto = new CategoryDto();
    when(categoryMapper.toEntity(dto)).thenReturn(testCategory);
    when(categoryRepository.save(testCategory)).thenReturn(testCategory);

    Category result = categoryService.create(dto);
    assertThat(result).isNotNull();
    verify(categoryRepository).save(testCategory);
  }

  @Test
  void shouldUpdate() {
    CategoryDto dto = new CategoryDto();
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
    when(categoryRepository.save(testCategory)).thenReturn(testCategory);

    Category result = categoryService.update(1L, dto);

    verify(categoryMapper).updateEntityFromDto(dto, testCategory);
    verify(categoryRepository).save(testCategory);
    assertThat(result).isNotNull();
  }

  @Test
  void shouldPatchBulk() {
    CategoryDto dto = new CategoryDto(); dto.setId(1L);
    when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
    when(categoryRepository.saveAll(anyList())).thenReturn(List.of(testCategory));

    List<Category> result = categoryService.patchBulk(List.of(dto));

    verify(categoryMapper).updateEntityFromDto(dto, testCategory);
    verify(categoryRepository).saveAll(anyList());
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldThrowWhenPatchBulkSizeMismatch() {
    CategoryDto dto = new CategoryDto(); dto.setId(1L);
    when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of());

    assertThatThrownBy(() -> categoryService.patchBulk(List.of(dto)))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldDeleteCategoryAndRelatedSkills() {
    Skill skill = new Skill();
    skill.setId(10L);
    testCategory.setSkills(Set.of(skill));

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

    categoryService.deleteById(1L);

    verify(skillService).deleteById(10L);
    verify(categoryRepository).delete(testCategory);
  }

  @Test
  void shouldDeleteAll() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
    categoryService.deleteAll(List.of(1L));
    verify(categoryRepository).delete(testCategory);
  }
}
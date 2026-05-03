package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.CategoryDto;
import com.antiprocrastinate.lab.mapper.CategoryMapper;
import com.antiprocrastinate.lab.model.Category;
import com.antiprocrastinate.lab.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Категории", description = "Управление категориями")
public class CategoryController {
  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @GetMapping
  @Operation(summary = "Получить все категории (с пагинацией)")
  public Page<CategoryDto> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return categoryService.findAll(PageRequest.of(page, size)).map(categoryMapper::toDto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить категорию по ID")
  public CategoryDto getById(@PathVariable Long id) {
    return categoryMapper.toDto(categoryService.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новую категорию")
  public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
    Category category = categoryMapper.toEntity(categoryDto);
    return categoryMapper.toDto(categoryService.save(category));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить категорию")
  public CategoryDto update(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
    Category category = categoryMapper.toEntity(categoryDto);
    category.setId(id);
    return categoryMapper.toDto(categoryService.save(category));
  }

  @PatchMapping("/bulk")
  @Operation(summary = "Массовое частичное обновление категорий")
  public List<CategoryDto> patchBulk(@RequestBody List<CategoryDto> dtos) {
    List<Category> updatedCategories = categoryService.patchBulk(dtos);
    return updatedCategories.stream().map(categoryMapper::toDto).toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить категорию")
  public void delete(@PathVariable Long id) {
    categoryService.deleteById(id);
  }
}
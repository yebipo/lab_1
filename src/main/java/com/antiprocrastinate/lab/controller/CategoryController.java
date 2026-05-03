package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.CategoryCreateDto;
import com.antiprocrastinate.lab.dto.CategoryResponseDto;
import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public PageResponse<CategoryResponseDto> getAll(Pageable pageable) {
    return PageResponse.of(categoryService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public CategoryResponseDto getById(@PathVariable Long id) {
    return categoryService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponseDto create(@Valid @RequestBody CategoryCreateDto dto) {
    return categoryService.create(dto);
  }

  @PutMapping("/{id}")
  public CategoryResponseDto update(
      @PathVariable Long id, @Valid @RequestBody CategoryCreateDto dto) {
    return categoryService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    categoryService.deleteById(id);
  }
}
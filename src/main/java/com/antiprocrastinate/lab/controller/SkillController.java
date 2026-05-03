package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.SkillCreateDto;
import com.antiprocrastinate.lab.dto.SkillResponseDto;
import com.antiprocrastinate.lab.service.SkillService;
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
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
  private final SkillService skillService;

  @GetMapping
  public PageResponse<SkillResponseDto> getAll(Pageable pageable) {
    return PageResponse.of(skillService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public SkillResponseDto getById(@PathVariable Long id) {
    return skillService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SkillResponseDto create(@Valid @RequestBody SkillCreateDto dto) {
    return skillService.create(dto);
  }

  @PutMapping("/{id}")
  public SkillResponseDto update(@PathVariable Long id, @Valid @RequestBody SkillCreateDto dto) {
    return skillService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    skillService.deleteById(id);
  }
}
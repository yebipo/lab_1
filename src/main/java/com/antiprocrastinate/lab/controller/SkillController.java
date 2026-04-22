package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.mapper.SkillMapper;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "Навыки", description = "Управление навыками")
public class SkillController {
  private final SkillService skillService;
  private final SkillMapper skillMapper;

  @GetMapping
  @Operation(summary = "Получить все навыки")
  public Set<SkillDto> getAll() {
    return skillService.findAll().stream()
        .map(skillMapper::toDto)
        .collect(Collectors.toSet());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить навык по ID")
  public SkillDto getById(@PathVariable Long id) {
    return skillMapper.toDto(skillService.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать новый навык")
  public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
    Skill skill = skillMapper.toEntity(skillDto);
    return skillMapper.toDto(skillService.save(skill));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить навык")
  public SkillDto update(@PathVariable Long id, @Valid @RequestBody SkillDto skillDto) {
    Skill skill = skillMapper.toEntity(skillDto);
    skill.setId(id);
    return skillMapper.toDto(skillService.save(skill));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить навык")
  public void delete(@PathVariable Long id) {
    skillService.deleteById(id);
  }
}
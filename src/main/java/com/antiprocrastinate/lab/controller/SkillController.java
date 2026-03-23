package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.SkillDto;
import com.antiprocrastinate.lab.mapper.SkillMapper;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.service.SkillService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
  private final SkillService skillService;
  private final SkillMapper skillMapper;

  @GetMapping
  public Set<SkillDto> getAll() {
    return skillService.findAll().stream()
        .map(skillMapper::toDto)
        .collect(Collectors.toSet());
  }

  @GetMapping("/{id}")
  public SkillDto getById(@PathVariable Long id) {
    return skillMapper.toDto(skillService.findById(id));
  }

  @PostMapping
  public SkillDto create(@RequestBody Skill skill) {
    return skillMapper.toDto(skillService.save(skill));
  }

  @PutMapping("/{id}")
  public SkillDto update(@PathVariable Long id, @RequestBody Skill skill) {
    skill.setId(id);
    return skillMapper.toDto(skillService.save(skill));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    skillService.deleteById(id);
  }
}
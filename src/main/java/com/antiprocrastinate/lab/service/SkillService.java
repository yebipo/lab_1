package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.SkillRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;

  @Transactional(readOnly = true)
  public Set<Skill> findAll() {
    return new HashSet<>(skillRepository.findAll());
  }

  @Transactional(readOnly = true)
  public Skill findById(Long id) {
    return skillRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
  }

  @Transactional
  public Skill save(Skill skill) {
    return skillRepository.save(skill);
  }

  @Transactional
  public void deleteById(Long id) {
    skillRepository.deleteById(id);
  }
}
package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.repository.SkillRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;

  public Set<Skill> findAll() {
    return new HashSet<>(skillRepository.findAll());
  }

  public Skill save(Skill skill) {
    return skillRepository.save(skill);
  }

  public void deleteById(Long id) {
    skillRepository.deleteById(id);
  }
}
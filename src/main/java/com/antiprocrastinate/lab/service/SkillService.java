package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.repository.SkillRepository;
import com.antiprocrastinate.lab.repository.TaskRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
  private final SkillRepository skillRepository;
  private final TaskRepository taskRepository;

  @Transactional(readOnly = true)
  public Page<Skill> findAll(Pageable pageable) {
    return skillRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Skill findById(Long id) {
    return skillRepository.findById(id)
        .orElseThrow(() ->new ResourceNotFoundException("Skill not found with id: " + id));
  }

  @Transactional
  public Skill save(Skill skill) {
    return skillRepository.save(skill);
  }

  @Transactional
  public void deleteById(Long id) {
    Skill skill = skillRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

    Set<Task> relatedTasks = new HashSet<>(skill.getTasks());

    for (Task task : relatedTasks) {
      task.getSkills().remove(skill);

      if (task.getSkills().isEmpty()) {
        taskRepository.delete(task);
      } else {
        taskRepository.save(task);
      }
    }

    skillRepository.delete(skill);
  }
}
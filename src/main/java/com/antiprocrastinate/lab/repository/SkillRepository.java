package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Skill;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface SkillRepository extends JpaRepository<Skill, Long> {

  @Override
  @EntityGraph(attributePaths = {"category"})
  Page<Skill> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"category", "tasks"})
  Optional<Skill> findById(Long id);
}
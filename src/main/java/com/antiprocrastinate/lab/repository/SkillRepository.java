package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Skill;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface SkillRepository extends JpaRepository<Skill, Long> {

  @Override
  @EntityGraph(attributePaths = {"category"})
  List<Skill> findAll();
}
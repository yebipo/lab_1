package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Skill;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface SkillRepository extends JpaRepository<Skill, Long> {
}
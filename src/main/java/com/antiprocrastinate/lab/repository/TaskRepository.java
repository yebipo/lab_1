package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Task;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface TaskRepository extends JpaRepository<Task, Long> {

  @Override
  @EntityGraph(attributePaths = {"user", "category", "skills"})
  List<Task> findAll();

  @Override
  @EntityGraph(attributePaths = {"user", "category", "skills"})
  Optional<Task> findById(Long id);

  List<Task> findByTitleContaining(String title);
}
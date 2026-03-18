package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Category;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface CategoryRepository extends JpaRepository<Category, Long> {

  @Override
  @EntityGraph(attributePaths = {"skills"})
  List<Category> findAll();
}
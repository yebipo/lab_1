package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Category;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface CategoryRepository extends JpaRepository<Category, Long> {

  @Override
  Page<Category> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"skills"})
  Optional<Category> findById(Long id);
}
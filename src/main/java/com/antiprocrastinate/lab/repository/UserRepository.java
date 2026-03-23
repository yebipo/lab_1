package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.User;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface UserRepository extends JpaRepository<User, Long> {

  @Override
  @EntityGraph(attributePaths = {"tasks"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"tasks"})
  Optional<User> findById(Long id);
}
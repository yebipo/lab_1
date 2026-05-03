package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.User;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface UserRepository extends JpaRepository<User, Long> {

  @Override
  Page<User> findAll(Pageable pageable);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
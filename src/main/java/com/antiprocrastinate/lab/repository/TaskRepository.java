package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Task;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
  Page<Task> findAllByUserId(Long userId, Pageable pageable);

  Optional<Task> findByIdAndUserId(Long id, Long userId);
}
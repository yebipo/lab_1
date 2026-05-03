package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.WorkLog;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

  @EntityGraph(attributePaths = {"task"})
  Page<WorkLog> findAllByTaskUserId(Long userId, Pageable pageable);

  @EntityGraph(attributePaths = {"task"})
  Optional<WorkLog> findByIdAndTaskUserId(Long id, Long userId);
}
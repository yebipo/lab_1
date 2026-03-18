package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.WorkLog;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

  @Override
  @EntityGraph(attributePaths = {"user", "task"})
  List<WorkLog> findAll();
}
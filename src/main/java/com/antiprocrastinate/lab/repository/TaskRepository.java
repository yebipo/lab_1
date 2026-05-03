package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  @Query("SELECT t FROM Task t JOIN t.skills s WHERE t.user.id = :userId AND s.id = :skillId")
  Page<Task> findTasksByUserAndSkillJpql(
      @Param("userId") Long userId,
      @Param("skillId") Long skillId,
      Pageable pageable);

  @Query(
      value = "SELECT t.* FROM tasks t " +
          "JOIN task_skills ts ON t.id = ts.task_id " +
          "WHERE t.user_id = :userId AND ts.skill_id = :skillId",
      nativeQuery = true)
  Page<Task> findTasksByUserAndSkillNative(
      @Param("userId") Long userId,
      @Param("skillId") Long skillId,
      Pageable pageable);
}
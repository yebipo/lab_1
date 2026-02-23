package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
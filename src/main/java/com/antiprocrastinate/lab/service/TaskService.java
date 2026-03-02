package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.Task;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final List<Task> storage = new ArrayList<>();

  public TaskService() {
    storage.add(createTask(1L, "Сдать лабу", "Дописать отчёт", 100, "ACTIVE"));
    storage.add(createTask(2L, "Покормить кошку", "Влажный корм, поменять воду", 10, "ANGRY"));
    storage.add(createTask(3L, "Не спать", "Скинуть лабу до дедлайна", 50, "ACTIVE"));
  }

  private Task createTask(Long id, String title, String desc, Integer score, String status) {
    Task task = new Task();
    task.setId(id);
    task.setTitle(title);
    task.setDescription(desc);
    task.setFocusScore(score);
    task.setStatus(status);
    return task;
  }

  public List<Task> getAllTasks() {
    return storage;
  }

  public Task getTaskById(Long id) {
    return storage.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  public List<Task> searchTasksByTitle(String title) {
    return storage.stream()
        .filter(t -> t.getTitle() != null
            && t.getTitle().toLowerCase().contains(title.toLowerCase()))
        .toList();
  }
}
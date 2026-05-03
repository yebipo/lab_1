package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.Task;
import com.antiprocrastinate.lab.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
  private TaskSpecifications() {}

  public static Specification<Task> hasUserId(Long userId) {
    return (root, query, cb)
        -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
  }

  public static Specification<Task> hasStatus(String status) {
    return (root, query, cb) -> {
      if (status == null) {
        return null;
      }
      try {
        return cb.equal(root.get("status"), TaskStatus.valueOf(status));
      } catch (IllegalArgumentException e) {
        return null;
      }
    };
  }

  public static Specification<Task> titleLike(String title) {
    return (root, query, cb) -> title == null || title.isBlank()
        ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<Task> hasSkillId(Long skillId) {
    return (root, query, cb) -> skillId == null
        ? null : cb.equal(root.join("skills").get("id"), skillId);
  }
}
package com.antiprocrastinate.lab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@SQLDelete(sql = "UPDATE tasks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  private String title;
  private String description;
  private Integer focusScore;

  @Builder.Default
  private boolean deleted = false;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "task_status_type")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Builder.Default
  private TaskStatus status = TaskStatus.TODO;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @BatchSize(size = 20)
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "task_skills",
      joinColumns = @JoinColumn(name = "task_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Builder.Default
  @ToString.Exclude
  private Set<Skill> skills = new HashSet<>();

  @BatchSize(size = 20)
  @OneToMany(mappedBy = "task", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @ToString.Exclude
  private Set<WorkLog> workLogs = new HashSet<>();
}
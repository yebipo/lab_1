package com.antiprocrastinate.lab.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Task {

  @Id
  private Long id;

  private String title;
  private String description;
  private Integer focusScore;
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getFocusScore() {
    return focusScore;
  }

  public void setFocusScore(Integer focusScore) {
    this.focusScore = focusScore;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
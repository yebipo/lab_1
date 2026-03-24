package com.antiprocrastinate.lab.util;

import java.util.Objects;

public class TaskSearchKey {
  private final Long userId;
  private final Long skillId;
  private final int page;
  private final int size;
  private final boolean useNative;

  public TaskSearchKey(Long userId, Long skillId, int page, int size, boolean useNative) {
    this.userId = userId;
    this.skillId = skillId;
    this.page = page;
    this.size = size;
    this.useNative = useNative;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TaskSearchKey that = (TaskSearchKey) o;
    return page == that.page
        && size == that.size
        && useNative == that.useNative
        && Objects.equals(userId, that.userId)
        && Objects.equals(skillId, that.skillId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, skillId, page, size, useNative);
  }
}
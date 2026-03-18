package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setLevel(user.getLevel());
    dto.setLevelUrl(user.getLevelUrl());
    dto.setDailyGoalMinutes(user.getDailyGoalMinutes());
    dto.setAvatarUrl(user.getAvatarUrl());
    return dto;
  }
}
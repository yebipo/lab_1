package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.model.User;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("DuplicatedCode")
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }
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

  public User toEntity(UserDto dto) {
    if (dto == null) {
      return null;
    }
    User user = new User();
    user.setId(dto.getId());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setLevel(dto.getLevel());
    user.setLevelUrl(dto.getLevelUrl());
    user.setDailyGoalMinutes(dto.getDailyGoalMinutes());
    user.setAvatarUrl(dto.getAvatarUrl());
    return user;
  }
}
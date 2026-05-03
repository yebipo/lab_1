package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);

  User toEntity(UserDto dto);
}
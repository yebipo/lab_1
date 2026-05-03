package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);

  User toEntity(UserDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(UserDto dto, @MappingTarget User user);
}
package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "skills", target = "skillIds")
  TaskDto toDto(Task task);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "skillIds", target = "skills")
  Task toEntity(TaskDto dto);

  @Mapping(source = "userId", target = "user.id",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "skillIds", target = "skills",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(TaskDto dto, @MappingTarget Task task);

  default Long skillToId(Skill skill) {
    return skill != null ? skill.getId() : null;
  }

  default Skill idToSkill(Long id) {
    if (id == null) {
      return null;
    }
    Skill s = new Skill();
    s.setId(id);
    return s;
  }
}
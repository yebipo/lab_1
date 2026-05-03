package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskCreateDto;
import com.antiprocrastinate.lab.dto.TaskResponseDto;
import com.antiprocrastinate.lab.dto.TaskUpdateDto;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillsToIds")
  TaskResponseDto toResponseDto(Task task);

  @Mapping(target = "user", ignore = true)
  @Mapping(source = "skillIds", target = "skills", qualifiedByName = "mapIdsToSkills")
  Task toEntity(TaskCreateDto dto);

  @Mapping(target = "user", ignore = true)
  @Mapping(source = "skillIds", target = "skills", qualifiedByName = "mapIdsToSkills")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

  @Named("mapSkillsToIds")
  default Set<Long> skillsToIds(Set<Skill> skills) {
    if (skills == null || skills.isEmpty()) {
      return new HashSet<>();
    }
    return skills.stream().map(Skill::getId).collect(Collectors.toSet());
  }

  @Named("mapIdsToSkills")
  default Set<Skill> idsToSkills(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return new HashSet<>();
    }
    return ids.stream().map(id -> {
      Skill s = new Skill();
      s.setId(id);
      return s;
    }).collect(Collectors.toSet());
  }
}
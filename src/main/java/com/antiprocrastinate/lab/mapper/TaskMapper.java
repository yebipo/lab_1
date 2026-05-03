package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.TaskDto;
import com.antiprocrastinate.lab.model.Skill;
import com.antiprocrastinate.lab.model.Task;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
    componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TaskMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "skills", target = "skillIds", qualifiedByName = "skillsToIds")
  TaskDto toDto(Task task);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "skillIds", target = "skills", qualifiedByName = "idsToSkills")
  Task toEntity(TaskDto dto);

  @Named("skillsToIds")
  default Set<Long> skillsToIds(Set<Skill> skills) {
    if (skills == null) {
      return Collections.emptySet();
    }
    return skills.stream().map(Skill::getId).collect(Collectors.toSet());
  }

  @Named("idsToSkills")
  default Set<Skill> idsToSkills(Set<Long> skillIds) {
    if (skillIds == null) {
      return Collections.emptySet();
    }
    return skillIds.stream().map(id -> {
      Skill skill = new Skill();
      skill.setId(id);
      return skill;
    }).collect(Collectors.toSet());
  }
}
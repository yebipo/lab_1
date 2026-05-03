package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.model.WorkLog;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

  @Mapping(source = "task.id", target = "taskId")
  WorkLogDto toDto(WorkLog workLog);

  @Mapping(source = "taskId", target = "task.id")
  WorkLog toEntity(WorkLogDto dto);

  @Mapping(source = "taskId", target = "task.id",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(WorkLogDto dto, @MappingTarget WorkLog workLog);
}
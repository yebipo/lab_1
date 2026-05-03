package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.WorkLogCreateDto;
import com.antiprocrastinate.lab.dto.WorkLogResponseDto;
import com.antiprocrastinate.lab.model.WorkLog;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {

  @Mapping(source = "task.id", target = "taskId")
  @Mapping(target = "endTime", ignore = true)
  WorkLogResponseDto toResponseDto(WorkLog workLog);

  @Mapping(source = "taskId", target = "task.id")
  WorkLog toEntity(WorkLogCreateDto dto);

  @Mapping(source = "taskId", target = "task.id")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(WorkLogCreateDto dto, @MappingTarget WorkLog workLog);

  @AfterMapping
  default void calculateEndTime(WorkLog entity, @MappingTarget WorkLogResponseDto dto) {
    if (entity.getStartTime() != null && entity.getDurationMinutes() != null) {
      dto.setEndTime(entity.getStartTime().plusMinutes(entity.getDurationMinutes()));
    }
  }
}
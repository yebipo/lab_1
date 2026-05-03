package com.antiprocrastinate.lab.mapper;

import com.antiprocrastinate.lab.dto.WorkLogDto;
import com.antiprocrastinate.lab.model.WorkLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkLogMapper {
  @Mapping(source = "task.id", target = "taskId")
  WorkLogDto toDto(WorkLog log);

  @Mapping(source = "taskId", target = "task.id")
  WorkLog toEntity(WorkLogDto dto);
}
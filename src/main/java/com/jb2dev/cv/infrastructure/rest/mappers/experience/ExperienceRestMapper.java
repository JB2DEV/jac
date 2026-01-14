package com.jb2dev.cv.infrastructure.rest.mappers.experience;

import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExperienceRestMapper {
  ExperienceResponse toResponse(ExperienceItem item);
  List<ExperienceResponse> toResponseList(List<ExperienceItem> items);
}

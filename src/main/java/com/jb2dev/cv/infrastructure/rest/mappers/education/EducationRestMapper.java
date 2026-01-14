package com.jb2dev.cv.infrastructure.rest.mappers.education;

import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.infrastructure.rest.dto.education.EducationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EducationRestMapper {
  EducationResponse toResponse(EducationItem item);
  List<EducationResponse> toResponseList(List<EducationItem> items);
}

package com.jb2dev.cv.infrastructure.rest.mappers.training;

import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingRestMapper {
  TrainingResponse toResponse(TrainingItem item);
  List<TrainingResponse> toResponseList(List<TrainingItem> items);
}

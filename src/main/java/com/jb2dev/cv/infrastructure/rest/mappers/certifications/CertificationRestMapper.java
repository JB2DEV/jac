package com.jb2dev.cv.infrastructure.rest.mappers.certifications;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.infrastructure.rest.dto.certifications.CertificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CertificationRestMapper {
  CertificationResponse toResponse(CertificationItem item);
  List<CertificationResponse> toResponseList(List<CertificationItem> items);
}

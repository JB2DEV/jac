package com.jb2dev.cv.infrastructure.rest.mappers.profile;

import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.infrastructure.rest.dto.profile.ContactInfoResponse;
import com.jb2dev.cv.infrastructure.rest.dto.profile.PersonalInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileRestMapper {
  PersonalInfoResponse toResponse(PersonalInfo info);
  ContactInfoResponse toResponse(ContactInfo info);
}

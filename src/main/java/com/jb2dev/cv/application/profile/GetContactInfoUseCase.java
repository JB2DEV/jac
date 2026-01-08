package com.jb2dev.cv.application.profile;

import com.jb2dev.cv.domain.profile.model.ContactInfo;

@FunctionalInterface
public interface GetContactInfoUseCase {
  ContactInfo execute();
}

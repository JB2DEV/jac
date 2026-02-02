package com.jb2dev.cv.application.profile;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;

@FunctionalInterface
public interface GetPersonalInfoUseCase {
  PersonalInfo execute(Language language);
}

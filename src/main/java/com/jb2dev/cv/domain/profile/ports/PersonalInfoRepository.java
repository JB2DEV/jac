package com.jb2dev.cv.domain.profile.ports;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;

public interface PersonalInfoRepository {
  PersonalInfo getPersonalInfo(Language language);
}

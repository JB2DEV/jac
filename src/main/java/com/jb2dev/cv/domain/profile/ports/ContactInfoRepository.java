package com.jb2dev.cv.domain.profile.ports;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;

public interface ContactInfoRepository {
  ContactInfo getContactInfo(Language language);
}

package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.application.profile.GetContactInfoUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetContactInfoInteractor implements GetContactInfoUseCase {

  private final ContactInfoRepository contactInfoRepository;

  @Override
  public ContactInfo execute(Language language) {
    return contactInfoRepository.getContactInfo(language);
  }
}

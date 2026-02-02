package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.application.profile.GetPersonalInfoUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetPersonalInfoInteractor implements GetPersonalInfoUseCase {

  private final PersonalInfoRepository personalInfoRepository;

  @Override
  public PersonalInfo execute(Language language) {
    return personalInfoRepository.getPersonalInfo(language);
  }
}

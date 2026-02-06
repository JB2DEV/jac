package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.application.profile.GetPersonalInfoUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetPersonalInfoInteractor implements GetPersonalInfoUseCase {

  private final PersonalInfoRepository personalInfoRepository;

  @Override
  public PersonalInfo execute(Language language) {
    log.info("Executing GetPersonalInfo use case for language: {}", language);
    PersonalInfo result = personalInfoRepository.getPersonalInfo(language);
    log.debug("Personal info retrieved: fullName={}, birthDate={}, nationality={}, gender={}",
        result.fullName(), result.birthDate(), result.nationality(), result.gender());
    return result;
  }
}

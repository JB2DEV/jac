package com.jb2dev.cv.application.profile;

import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetPersonalInfoService implements GetPersonalInfoUseCase {

  private final PersonalInfoQueryPort queryPort;

  @Override
  public PersonalInfo execute() {
    return queryPort.getPersonalInfo();
  }
}

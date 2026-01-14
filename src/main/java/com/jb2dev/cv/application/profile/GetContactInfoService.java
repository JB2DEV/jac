package com.jb2dev.cv.application.profile;

import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetContactInfoService implements GetContactInfoUseCase {

  private final ContactInfoQueryPort queryPort;

  @Override
  public ContactInfo execute() {
    return queryPort.getContactInfo();
  }
}

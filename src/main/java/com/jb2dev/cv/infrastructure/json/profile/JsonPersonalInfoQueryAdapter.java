package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JsonPersonalInfoQueryAdapter implements PersonalInfoRepository {

  private final ClasspathJsonReader reader;

  @Override
  public PersonalInfo getPersonalInfo() {
    return reader.read("data/personal.json", PersonalInfo.class);
  }
}

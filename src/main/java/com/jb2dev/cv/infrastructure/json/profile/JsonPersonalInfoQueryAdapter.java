package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsonPersonalInfoQueryAdapter implements PersonalInfoRepository {

  private final ClasspathJsonReader reader;

  @Override
  public PersonalInfo getPersonalInfo(Language language) {
    String path = language == Language.EN_EN ? "data/en/personal.json" : "data/es/personal.json";
    log.debug("Reading personal info from JSON file: {}", path);
    return reader.read(path, PersonalInfo.class);
  }
}

package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsonContactInfoQueryAdapter implements ContactInfoRepository {

  private final ClasspathJsonReader reader;

  @Override
  public ContactInfo getContactInfo(Language language) {
    String path = language == Language.EN_EN ? "data/en/contact.json" : "data/es/contact.json";
    log.debug("Reading contact info from JSON file: {}", path);
    return reader.read(path, ContactInfo.class);
  }
}

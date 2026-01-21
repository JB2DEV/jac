package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JsonContactInfoQueryAdapter implements ContactInfoRepository {

  private final ClasspathJsonReader reader;

  @Override
  public ContactInfo getContactInfo() {
    return reader.read("data/contact.json", ContactInfo.class);
  }
}

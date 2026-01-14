package com.jb2dev.cv.infrastructure.json.certifications;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.domain.certifications.ports.CertificationQueryPort;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonCertificationQueryAdapter implements CertificationQueryPort {

  private static final TypeReference<List<CertificationItem>> TYPE = new TypeReference<>() {};
  private final ClasspathJsonReader reader;

  @Override
  public List<CertificationItem> list() {
    return reader.read("data/certifications.json", TYPE);
  }

  @Override
  public Optional<CertificationItem> findById(int id) {
    return list().stream().filter(i -> i.id() == id).findFirst();
  }
}

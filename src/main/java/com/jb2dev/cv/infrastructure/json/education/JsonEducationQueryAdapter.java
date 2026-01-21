package com.jb2dev.cv.infrastructure.json.education;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonEducationQueryAdapter implements EducationRepository {

  private static final TypeReference<List<EducationItem>> TYPE = new TypeReference<>() {};
  private final ClasspathJsonReader reader;

  @Override
  public List<EducationItem> findAllEducations() {
    var items = reader.read("data/education.json", TYPE);
    return items.stream()
        .sorted(Comparator.comparing(EducationItem::startDate).reversed())
        .toList();
  }

  @Override
  public Optional<EducationItem> findEducationById(int id) {
    return findAllEducations().stream().filter(i -> i.id() == id).findFirst();
  }
}

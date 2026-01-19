package com.jb2dev.cv.infrastructure.json.experience;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceQueryPort;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonExperienceQueryAdapter implements ExperienceQueryPort {

  private static final TypeReference<List<ExperienceItem>> TYPE = new TypeReference<>() {};
  private final ClasspathJsonReader reader;

  @Override
  public List<ExperienceItem> list() {
    var items = reader.read("data/experience.json", TYPE);
    return items.stream()
        .sorted(Comparator.comparing(ExperienceItem::startDate).reversed())
        .toList();
  }

  @Override
  public Optional<ExperienceItem> findById(int id) {
    return list().stream().filter(i -> i.id() == id).findFirst();
  }
}

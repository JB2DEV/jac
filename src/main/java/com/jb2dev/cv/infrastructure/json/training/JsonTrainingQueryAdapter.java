package com.jb2dev.cv.infrastructure.json.training;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonTrainingQueryAdapter implements TrainingRepository {

  private static final TypeReference<List<TrainingItem>> TYPE = new TypeReference<>() {};
  private final ClasspathJsonReader reader;

  @Override
  public List<TrainingItem> findAllTrainings(Language language) {
    String path = language == Language.EN_EN ? "data/en/training.json" : "data/es/training.json";
    var items = reader.read(path, TYPE);
    return items.stream()
        .sorted(Comparator.comparing(TrainingItem::issuedDate).reversed())
        .toList();
  }

  @Override
  public Optional<TrainingItem> findTrainingById(String credentialId, Language language) {
    return findAllTrainings(language).stream().filter(i -> Objects.equals(i.credentialId(), credentialId)).findFirst();
  }
}

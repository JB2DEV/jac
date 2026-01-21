package com.jb2dev.cv.infrastructure.json.training;

import com.fasterxml.jackson.core.type.TypeReference;
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
  public List<TrainingItem> findAllTrainings() {
    var items = reader.read("data/training.json", TYPE);
    return items.stream()
        .sorted(Comparator.comparing(TrainingItem::issuedDate).reversed())
        .toList();
  }

  @Override
  public Optional<TrainingItem> findTrainingById(String credentialId) {
    return findAllTrainings().stream().filter(i -> Objects.equals(i.credentialId(), credentialId)).findFirst();
  }
}

package com.jb2dev.cv.infrastructure.json.training;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingQueryPort;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonTrainingQueryAdapter implements TrainingQueryPort {

  private static final TypeReference<List<TrainingItem>> TYPE = new TypeReference<>() {};
  private final ClasspathJsonReader reader;

  @Override
  public List<TrainingItem> list() {
    var items = reader.read("data/training.json", TYPE);
    return items.stream()
        .sorted(Comparator.comparing(TrainingItem::startDate).reversed())
        .toList();
  }

  @Override
  public Optional<TrainingItem> findById(int id) {
    return list().stream().filter(i -> i.id() == id).findFirst();
  }
}

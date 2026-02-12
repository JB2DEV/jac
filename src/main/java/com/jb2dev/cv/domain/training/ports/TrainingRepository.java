package com.jb2dev.cv.domain.training.ports;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
  List<TrainingItem> findAllTrainings(Language language);
  Optional<TrainingItem> findTrainingById(String credentialId, Language language);
}

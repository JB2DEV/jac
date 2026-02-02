package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.Optional;

@FunctionalInterface
public interface GetTrainingUseCase {
  Optional<TrainingItem> execute(String credentialId, Language language);
}

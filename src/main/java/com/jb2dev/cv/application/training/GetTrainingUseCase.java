package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;

@FunctionalInterface
public interface GetTrainingUseCase {
  TrainingItem execute(String credentialId, Language language);
}

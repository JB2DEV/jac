package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.Optional;

@FunctionalInterface
public interface GetTrainingByIdUseCase {
  Optional<TrainingItem> execute(String credentialId);
}

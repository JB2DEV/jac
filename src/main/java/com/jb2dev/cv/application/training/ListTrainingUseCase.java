package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.List;

@FunctionalInterface
public interface ListTrainingUseCase {
  List<TrainingItem> execute();
}

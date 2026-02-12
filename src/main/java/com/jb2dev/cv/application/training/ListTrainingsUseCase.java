package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.List;

@FunctionalInterface
public interface ListTrainingsUseCase {
  List<TrainingItem> execute(Language language);
}

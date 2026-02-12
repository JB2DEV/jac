package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.application.training.ListTrainingsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListTrainingsInteractor implements ListTrainingsUseCase {

  private final TrainingRepository trainingRepository;

  @Override
  public List<TrainingItem> execute(Language language) {
    log.info("Executing ListTrainings use case for language: {}", language);
    List<TrainingItem> result = trainingRepository.findAllTrainings(language);
    log.debug("Retrieved {} training items", result.size());
    return result;
  }
}

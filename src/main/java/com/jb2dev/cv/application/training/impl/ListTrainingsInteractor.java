package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.application.training.ListTrainingsUseCase;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListTrainingsInteractor implements ListTrainingsUseCase {

  private final TrainingRepository trainingRepository;

  @Override
  public List<TrainingItem> execute() {
    return trainingRepository.findAllTrainings();
  }
}

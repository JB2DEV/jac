package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.application.training.GetTrainingUseCase;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetTrainingInteractor implements GetTrainingUseCase {

  private final TrainingRepository trainingRepository;

  @Override
  public Optional<TrainingItem> execute(String credentialId) {
    return trainingRepository.findTrainingById(credentialId);
  }
}

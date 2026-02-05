package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.application.training.GetTrainingUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTrainingInteractor implements GetTrainingUseCase {

  private final TrainingRepository trainingRepository;

  @Override
  public TrainingItem execute(String credentialId, Language language) {
    return trainingRepository.findTrainingById(credentialId, language)
        .orElseThrow(() -> new ResourceNotFoundException("Training", credentialId));
  }
}

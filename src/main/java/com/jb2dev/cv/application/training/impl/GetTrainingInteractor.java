package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.application.training.GetTrainingUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetTrainingInteractor implements GetTrainingUseCase {

  private final TrainingRepository trainingRepository;

  @Override
  public TrainingItem execute(String credentialId, Language language) {
    log.info("Executing GetTraining use case: credentialId={}, language={}", credentialId, language);

    TrainingItem result = trainingRepository.findTrainingById(credentialId, language)
        .orElseThrow(() -> {
          log.warn("Training not found: credentialId={}, language={}", credentialId, language);
          return new ResourceNotFoundException("Training", credentialId);
        });

    log.debug("Training retrieved: id={}, title={}, provider={}, location={}, issuedDate={}, credentialUrl={}",
        result.id(), result.title(), result.provider(), result.location(), result.issuedDate(), result.credentialUrl());
    return result;
  }
}

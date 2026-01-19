package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetTrainingByIdService implements GetTrainingByIdUseCase {

  private final TrainingQueryPort queryPort;

  @Override
  public Optional<TrainingItem> execute(String credentialId) {
    return queryPort.findById(credentialId);
  }
}

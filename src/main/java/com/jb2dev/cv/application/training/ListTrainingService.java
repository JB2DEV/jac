package com.jb2dev.cv.application.training;

import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListTrainingService implements ListTrainingUseCase {

  private final TrainingQueryPort queryPort;

  @Override
  public List<TrainingItem> execute() {
    return queryPort.list();
  }
}
